package ru.nsu.logic.lang.execution.blockchain;

import lombok.Getter;
import ru.nsu.logic.lang.execution.blockchain.common.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Transaction implements ITransaction {
    @Getter
    private final ITransactionInfo transactionInfo;
    @Getter
    private final IBlockchainBlock blockchainBlock;
    @Getter
    private final Map<ISmartContractMemberId, ISmartContractMember> storage;

    public Transaction(final ITransactionInfo transactionInfo, final IBlockchainBlock blockchainBlock) {
        this.transactionInfo = transactionInfo;
        this.blockchainBlock = blockchainBlock;
        this.storage = new HashMap<>();
    }

    @Override
    public void startTransaction() {}

    @Override
    public void endTransaction() {
        blockchainBlock.addTransaction(this);
    }

    @Override
    public void store(final ISmartContractMember member) {
        storage.put(member.getId(), member);
    }

    @Override
    public Optional<ISmartContractMember> lookup(final ISmartContractMemberId memberId) {
        final ISmartContractMember member = storage.get(memberId);
        return member != null ? Optional.of(member) : Optional.empty();
    }
}
