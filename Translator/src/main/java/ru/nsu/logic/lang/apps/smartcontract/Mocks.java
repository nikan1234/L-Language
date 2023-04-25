package ru.nsu.logic.lang.apps.smartcontract;

import lombok.AllArgsConstructor;
import ru.nsu.logic.lang.compilation.common.ICompiledProgram;
import ru.nsu.logic.lang.execution.blockchain.BlockchainBlock;
import ru.nsu.logic.lang.execution.blockchain.SmartContractInstance;
import ru.nsu.logic.lang.execution.blockchain.TransactionQueue;
import ru.nsu.logic.lang.execution.blockchain.common.*;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

public class Mocks {

    public static class TestBlockchain implements IBlockchainAPI {

        private final ICompiledProgram program;
        private final List<IBlockchainBlock> blockchain;

        public TestBlockchain(final ICompiledProgram program) {
            this.program = program;
            this.blockchain = new LinkedList<>();
        }

        @Override
        public void pushBlock(final IBlockchainBlock block) {
            blockchain.add(block);
        }

        @Override
        public Optional<ISmartContractInstance> findSmartContract(final ITransactionInfo transactionInfo) {
            return Optional.of(new SmartContractInstance(transactionInfo, program));
        }

        @Override
        public Optional<ISmartContractMember> findMember(final ITransactionInfo transactionInfo,
                                                         final ISmartContractMemberId memberId) {
            // Generate an iterator. Start just after the last element.
            final ListIterator<IBlockchainBlock> li = blockchain.listIterator(blockchain.size());
            while(li.hasPrevious()) {
                final Optional<ISmartContractMember> member =  li.previous().findMember(transactionInfo, memberId);
                if (member.isPresent())
                    return member;
            }
            return Optional.empty();
        }
    }

    @AllArgsConstructor
    public static class TestBlockController implements TransactionQueue.IBlockController {

        private final TestBlockchain blockchain;

        @Override
        public IBlockchainBlock getBlock(final ITransactionQueue queue) {
            return new BlockchainBlock(queue, blockchain);
        }

        @Override
        public void onBlockUpdated(final IBlockchainBlock block) {
            blockchain.pushBlock(block);
        }
    }

}
