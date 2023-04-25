package ru.nsu.logic.lang.execution.blockchain;

import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.compilation.statements.ListValueStatement;
import ru.nsu.logic.lang.compilation.statements.NullValueStatement;
import ru.nsu.logic.lang.compilation.statements.NumberValueStatement;
import ru.nsu.logic.lang.compilation.statements.ObjectValueStatement;
import ru.nsu.logic.lang.execution.blockchain.common.ISmartContractMember;
import ru.nsu.logic.lang.execution.blockchain.common.ISmartContractMemberId;
import ru.nsu.logic.lang.execution.blockchain.members.MemberAdapter;


class SmartContractMemberFactory {

    public static class SingletonHolder {
        public static final SmartContractMemberFactory HOLDER_INSTANCE = new SmartContractMemberFactory();
    }

    public static SmartContractMemberFactory getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    ISmartContractMember fromStatement(final ISmartContractMemberId memberId, final IStatement statement) {
        /*if (statement instanceof NumberValueStatement)
            return new NumericMember();
        if (statement instanceof ListValueStatement)
            return new ListMember();
        if (statement instanceof ObjectValueStatement)
            return new ObjectMember();*/

        if (statement instanceof NumberValueStatement ||
            statement instanceof ListValueStatement ||
            statement instanceof ObjectValueStatement ||
            statement instanceof NullValueStatement)
            return new MemberAdapter(memberId, statement);

        throw new RuntimeException("Unknown statement given: " + statement);
    }
}
