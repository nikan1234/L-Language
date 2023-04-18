package ru.nsu.logic.lang.execution.blockchain.common;

import ru.nsu.logic.lang.compilation.common.IStatement;


public interface ITransaction {
    ITransactionInfo getTransactionInfo();

    void startTransaction();
    void endTransaction();

    IStatement getContractMember(final String name);
    void setContractMember(final String name, final IStatement statement);
}
