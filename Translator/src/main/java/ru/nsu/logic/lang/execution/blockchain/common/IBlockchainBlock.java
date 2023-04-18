package ru.nsu.logic.lang.execution.blockchain.common;


public interface IBlockchainBlock extends IContractMemberProvider {
    void addTransaction(final ITransaction transaction);
}
