package ru.nsu.logic.lang.execution.blockchain;

import ru.nsu.logic.lang.execution.blockchain.common.IBlockchainBlock;
import ru.nsu.logic.lang.execution.blockchain.common.ITransaction;
import ru.nsu.logic.lang.execution.blockchain.common.ITransactionInfo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BlockchainBlock implements IBlockchainBlock {

    private final List<ITransaction> transactions;

    public BlockchainBlock() {
        this.transactions = new ArrayList<>();
    }

}
