package ru.nsu.logic.lang.execution.blockchain.common;


import java.util.Optional;

public interface ITransaction {
    ITransactionInfo getTransactionInfo();
    IBlockchainBlock getBlockchainBlock();

    void startTransaction();
    void endTransaction();

    Optional<ISmartContractMember> lookup(final ISmartContractMemberId memberId);
    void store(final ISmartContractMember member);
}
