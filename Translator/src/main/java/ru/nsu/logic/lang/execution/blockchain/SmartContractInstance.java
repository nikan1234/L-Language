package ru.nsu.logic.lang.execution.blockchain;


import ru.nsu.logic.lang.common.AccessType;
import ru.nsu.logic.lang.compilation.common.*;
import ru.nsu.logic.lang.compilation.statements.ObjectValueStatement;
import ru.nsu.logic.lang.execution.blockchain.common.*;
import ru.nsu.logic.lang.execution.common.ExecutionException;

import java.util.*;

public class SmartContractInstance implements ISmartContractInstance {

    private final ICompiledProgram smartContractProgram;
    private final ICompiledClass smartContractClass;
    private final ICompiledMethod smartContractMethod;

    public SmartContractInstance(final ITransactionInfo transactionInfo,
                                 final ICompiledProgram smartContractProgram) {

        try {
            final String contractName = transactionInfo.getSmartContractId().getContractName();
            final String methodName = transactionInfo.getSmartContractMethodId().getMethodName();

            final Optional<ICompiledClass> smartContract = smartContractProgram.getCompiledClasses().lookup(contractName);
            if (!smartContract.isPresent())
                throw new RuntimeException("Smart contract " + contractName + " not found");

            this.smartContractProgram = smartContractProgram;
            this.smartContractClass = smartContract.get();
            if (ICompiledClass.CTOR_NAME.equals(methodName)) {
                final Optional<ICompiledMethod> constructor = smartContract.get().getConstructor(
                        AccessType.Masks.ONLY_PUBLIC);
                if (!constructor.isPresent())
                    throw new ExecutionException("Smart contract constructor not found");
                this.smartContractMethod = constructor.get();
            }
            else
                this.smartContractMethod = smartContract.get().accessMethod(methodName, AccessType.Masks.ONLY_PUBLIC);

        }
        catch (final ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IObject getInstance(final ITransaction transaction) {
        return new ObjectValueStatement(smartContractClass, new IObjectMemberStorage() {

            private final ISmartContractId smartContractId = new SmartContractId(smartContractClass.getName());
            private final HashMap<IMember, IStatement> cache = new HashMap<>();
            private final SmartContractMemberFactory factory = SmartContractMemberFactory.getInstance();

            @Override
            public void store(final IMember member, final IStatement statement) {
                cache.put(member, statement);
                transaction.store(factory.fromStatement(
                        new SmartContractMemberId(smartContractId, member.getName()), statement));
            }

            @Override
            public IStatement lookup(IMember member) {
                final IStatement found = cache.get(member);
                if (found != null)
                    return found;

                final ISmartContractMemberProvider[] providers = {
                        transaction.getBlockchainBlock(),
                        transaction.getBlockchainBlock().getBlockchainAPI()
                };

                for (final ISmartContractMemberProvider provider : providers) {
                    final Optional<ISmartContractMember> memberValue = provider
                            .findMember(transaction.getTransactionInfo(),
                                    new SmartContractMemberId(smartContractId, member.getName()));
                    if (!memberValue.isPresent())
                        continue;

                    final IStatement statement = memberValue.get().asStatement();
                    cache.put(member, statement);
                    return statement;
                }
                throw new RuntimeException("Internal error: member " + member.getName() + " not found in blockchain");
            }
        }, smartContractClass.getLocation());
    }

    @Override
    public ICompiledProgram getContractProgram() {
        return smartContractProgram;
    }

    @Override
    public ICompiledMethod getContractMethod() {
        return smartContractMethod;
    }
}
