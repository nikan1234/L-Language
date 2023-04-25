package ru.nsu.logic.lang.execution.blockchain;

import ru.nsu.logic.lang.execution.blockchain.common.*;

import java.util.*;

public class TransactionQueue implements ITransactionQueue {

    public interface IBlockController {
        IBlockchainBlock getBlock(final ITransactionQueue queue);
        void onBlockUpdated(final IBlockchainBlock block);
    }

    private final IBlockController controller;
    private final LinkedHashMap<ITransactionInfo, ITransaction> transactions;
    private final Deque<ITransaction> pendingTransactions;


    public TransactionQueue(final IBlockController controller) {
        this.controller = controller;
        this.transactions = new LinkedHashMap<>();
        this.pendingTransactions = new LinkedList<>();
    }

    @Override
    public void queryTransaction(final ITransactionInfo transactionInfo) {
        final Transaction transaction = new Transaction(transactionInfo, controller.getBlock(this));
        transactions.put(transactionInfo, transaction);
        pendingTransactions.push(transaction);
    }

    @Override
    public void onTransactionCompleted(final ITransactionInfo transactionInfo) {
        final ITransaction transaction = transactions.remove(transactionInfo);
        controller.onBlockUpdated(transaction.getBlockchainBlock());
    }

    @Override
    public ITransaction getNext() {
        return pendingTransactions.pop();
    }
}
