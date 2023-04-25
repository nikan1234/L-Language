package ru.nsu.logic.lang.execution.blockchain.members;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.blockchain.common.ISmartContractMember;
import ru.nsu.logic.lang.execution.blockchain.common.ISmartContractMemberId;

@AllArgsConstructor
public class MemberAdapter implements ISmartContractMember {

    @Getter
    private final ISmartContractMemberId id;
    private final IStatement statement;

    @Override
    public IStatement asStatement() {
        return statement;
    }
}
