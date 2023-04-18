package ru.nsu.logic.lang.execution.blockchain;


import ru.nsu.logic.lang.common.AccessType;
import ru.nsu.logic.lang.compilation.common.ICompiledClass;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.compilation.statements.ObjectValue;
import ru.nsu.logic.lang.execution.blockchain.common.ITransaction;
import ru.nsu.logic.lang.execution.common.ExecutionException;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class SmartContractInstance extends ObjectValue {

    private final ITransaction transaction;
    private final Set<String> cachedMembers;

    public SmartContractInstance(final ICompiledClass contract, final ITransaction transaction) {
        super(contract, contract.getLocation());
        this.transaction = transaction;
        this.cachedMembers = new HashSet<>();
    }

    public IStatement getMemberValue(final String memberName,
                                     final EnumSet<AccessType> accessMask) throws ExecutionException {
        super.validateAccess(memberName, accessMask);

        if (cachedMembers.contains(memberName))
            return super.getMemberValue(memberName, accessMask);

        final IStatement value = transaction.getContractMember(memberName);
        super.setMemberValue(memberName, value, accessMask);
        cachedMembers.add(memberName);
        return value;
    }

    public void setMemberValue(final String memberName,
                               final IStatement statement,
                               final EnumSet<AccessType> accessMask) throws ExecutionException {
        super.setMemberValue(memberName, statement, accessMask);
        transaction.setContractMember(memberName, statement);
    }
}
