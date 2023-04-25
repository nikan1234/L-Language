package ru.nsu.logic.lang.execution.blockchain;


import ru.nsu.logic.lang.compilation.common.*;
import ru.nsu.logic.lang.compilation.statements.ObjectValueStatement;
import ru.nsu.logic.lang.execution.blockchain.common.*;

import java.util.*;

public class SmartContractInstance implements ISmartContractInstance {

    private final ICompiledProgram smartContractProgram;
    private final ICompiledClass smartContractClass;
    private final ICompiledMethod smartContractMethod;

    public SmartContractInstance(final ITransactionInfo transactionInfo, final ICompiledProgram smartContractProgram) {
        final String contractName = transactionInfo.getSmartContractId().getContractName();
        final String methodName = transactionInfo.getSmartContractMethodId().getMethodName();

        final Optional<ICompiledClass> smartContract = smartContractProgram.getCompiledClasses().lookup(contractName);
        if (!smartContract.isPresent())
            throw new RuntimeException("Smart contract " + contractName + " not found");

        final Optional<ICompiledMethod> smartContractMethod = smartContract.get().getMethod(methodName);
        if (!smartContractMethod.isPresent())
            throw new RuntimeException("Smart contract method" + methodName + " not found for contract " + contractName);

        this.smartContractProgram = smartContractProgram;
        this.smartContractClass = smartContract.get();
        this.smartContractMethod = smartContractMethod.get();
    }


    @Override
    public IObject getInstance(final ITransaction transaction) {
        return new ObjectValueStatement(smartContractClass, new IObject.IMemberStorage() {

            private final HashMap<String, IStatement> cache = new HashMap<>();
            private final SmartContractMemberFactory factory = SmartContractMemberFactory.getInstance();

            @Override
            public IStatement lookup(final String memberName) {
                final IStatement found = cache.get(memberName);
                if (found != null)
                    return found;

                final ISmartContractMemberProvider[] providers = {
                        transaction.getBlockchainBlock(),
                        transaction.getBlockchainBlock().getBlockchainAPI()
                };

                for (final ISmartContractMemberProvider provider : providers) {
                    final Optional<ISmartContractMember> member = provider
                            .findMember(transaction.getTransactionInfo(), new SmartContractMemberId(memberName));
                    if (!member.isPresent())
                        continue;

                    final IStatement statement = member.get().asStatement();
                    cache.put(memberName, statement);
                    return statement;
                }
                throw new RuntimeException("Internal error: member " + memberName + " not found in blockchain");
            }

            @Override
            public void store(final String memberName, final IStatement statement) {
                cache.put(memberName, statement);
                transaction.store(factory.fromStatement(new SmartContractMemberId(memberName), statement));
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
