package ru.nsu.logic.lang.execution.blockchain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.blockchain.common.IBlockchainBlock;
import ru.nsu.logic.lang.execution.blockchain.common.ITransaction;
import ru.nsu.logic.lang.execution.blockchain.common.ITransactionInfo;

@AllArgsConstructor
public class Transaction implements ITransaction {

    private final IBlockchainBlock block;
    @Getter
    private final ITransactionInfo transactionInfo;


    @Override
    public void startTransaction() {

    }

    @Override
    public void endTransaction() {
        block.addTransaction(this);
    }

    @Override
    public IStatement getContractMember(String name) {
        return null;
    }

    @Override
    public void setContractMember(String name, IStatement statement) {

    }
}
