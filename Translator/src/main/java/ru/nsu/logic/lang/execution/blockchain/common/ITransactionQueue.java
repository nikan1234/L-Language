package ru.nsu.logic.lang.execution.blockchain.common;

public interface ITransactionQueue {
    void queryTransaction(final ITransactionInfo transactionInfo);
    void onTransactionCompleted(final ITransactionInfo transactionInfo);

    ITransaction getNext();
}
