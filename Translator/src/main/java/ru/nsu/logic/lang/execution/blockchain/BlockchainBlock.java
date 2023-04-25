package ru.nsu.logic.lang.execution.blockchain;

import ru.nsu.logic.lang.execution.blockchain.common.*;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;

public class BlockchainBlock implements IBlockchainBlock {

    private final ITransactionQueue queue;
    private final IBlockchainAPI blockchainAPI;
    private final ConcurrentLinkedDeque<ITransaction> transactions;


    public BlockchainBlock(final ITransactionQueue queue,
                           final IBlockchainAPI blockchainAPI) {
        this.queue = queue;
        this.blockchainAPI = blockchainAPI;
        this.transactions = new ConcurrentLinkedDeque<>();
    }

    @Override
    public IBlockchainAPI getBlockchainAPI() {
        return blockchainAPI;
    }

    @Override
    public void addTransaction(final ITransaction transaction) {
        this.transactions.push(transaction);
        this.queue.onTransactionCompleted(transaction.getTransactionInfo());
    }

    @Override
    public Optional<ISmartContractMember> findMember(final ITransactionInfo transactionInfo,
                                                     final ISmartContractMemberId memberId) {

        final Iterator<ITransaction> iterator = this.transactions.descendingIterator();

        while (iterator.hasNext()) {
            final ITransaction transaction = iterator.next();
            final ISmartContractId id = transaction.getTransactionInfo().getSmartContractId();

            if (transactionInfo.getSmartContractId().equals(id)) {
                final Optional<ISmartContractMember> member = transaction.lookup(memberId);
                if (member.isPresent())
                    return member;
            }
        }
        return Optional.empty();
    }
}
