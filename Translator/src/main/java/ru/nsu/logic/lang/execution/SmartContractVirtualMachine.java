package ru.nsu.logic.lang.execution;

import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.blockchain.common.*;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IPipeline;

import java.util.HashMap;
import java.util.Map;


public class SmartContractVirtualMachine extends VirtualMachineBase {
    private final ISmartContractInstance instance;
    private final ITransaction transaction;


    public SmartContractVirtualMachine(final ITransaction transaction) {
        this(transaction, transaction.getBlockchainBlock().getBlockchainAPI()
                .findSmartContract(transaction.getTransactionInfo())
                .orElseThrow(RuntimeException::new));
    }

    private SmartContractVirtualMachine(final ITransaction transaction, final ISmartContractInstance instance) {
        super(instance.getContractProgram());
        this.instance = instance;
        this.transaction = transaction;
    }

    @Override
    public void run() throws ExecutionException {
        this.transaction.startTransaction();
        super.run();
    }

    @Override
    protected void initializePipeline(IPipeline pipeline) {
        final ITransactionInfo info = transaction.getTransactionInfo();

        final Map<String, IStatement> init = new HashMap<>(info.getParameters());
        init.put("this", instance.getInstance(transaction));

        final Context context = Context.CreateForClassMethod(
                info.getSmartContractId().getContractName(),
                info.getSmartContractMethodId().getMethodName());

        pipeline.pushEntry(new PipelineEntry(context, init, instance.getContractMethod().getBody()));
    }

    @Override
    protected void shutdown() throws ExecutionException {
        super.shutdown();
        this.transaction.endTransaction();
    }
}
