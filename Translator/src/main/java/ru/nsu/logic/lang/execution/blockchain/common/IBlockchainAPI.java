package ru.nsu.logic.lang.execution.blockchain.common;

import java.util.Optional;

public interface IBlockchainAPI extends ISmartContractMemberProvider {
    void pushBlock(final IBlockchainBlock block);
    Optional<ISmartContractInstance> findSmartContract(final ITransactionInfo transactionInfo);
}
