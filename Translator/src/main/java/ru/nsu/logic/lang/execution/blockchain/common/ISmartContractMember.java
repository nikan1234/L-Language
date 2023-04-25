package ru.nsu.logic.lang.execution.blockchain.common;

import ru.nsu.logic.lang.compilation.common.IStatement;

public interface ISmartContractMember {
    ISmartContractMemberId getId();
    IStatement asStatement();
}
