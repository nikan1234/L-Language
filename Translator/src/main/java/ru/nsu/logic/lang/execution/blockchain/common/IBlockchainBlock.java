package ru.nsu.logic.lang.execution.blockchain.common;


public interface IBlockchainBlock extends ISmartContractMemberProvider {
    IBlockchainAPI getBlockchainAPI();

    void addTransaction(final ITransaction transaction);
}
