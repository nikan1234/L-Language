package ru.nsu.logic.lang.execution;

import lombok.AllArgsConstructor;
import ru.nsu.logic.lang.compilation.common.ICompiledClass;
import ru.nsu.logic.lang.compilation.common.ICompiledMethod;
import ru.nsu.logic.lang.compilation.common.ICompiledProgram;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.blockchain.SmartContractInstance;
import ru.nsu.logic.lang.execution.blockchain.common.ITransaction;
import ru.nsu.logic.lang.execution.blockchain.common.ITransactionInfo;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IPipeline;

import java.util.HashMap;
import java.util.Optional;

public class SmartContractVirtualMachine extends VirtualMachineBase {

    private final ICompiledProgram program;
    private final ITransaction transaction;

    public SmartContractVirtualMachine(final ICompiledProgram program, final ITransaction transaction) {
        super(program);
        this.program = program;
        this.transaction = transaction;
    }

    @Override
    public void run() throws ExecutionException {
        this.transaction.startTransaction();
        super.run();
    }

    @Override
    protected void initializePipeline(IPipeline pipeline) throws ExecutionException {
        final ITransactionInfo info = transaction.getTransactionInfo();

        final Optional<ICompiledClass> compiledClass = program.getCompiledClasses().lookup(info.getContractName());
        if (!compiledClass.isPresent())
            throw new ExecutionException("Smart contract with name " + info.getContractName() + " not found");

        final Optional<ICompiledMethod> method = compiledClass.get().getMethod(info.getMethodName());
        if (!method.isPresent())
            throw new ExecutionException("Method with name " + info.getMethodName() + " not found");

        final HashMap<String, IStatement> init = new HashMap<>(info.getParameters());
        init.put("this", new SmartContractInstance(compiledClass.get(), transaction));
        pipeline.pushEntry(new PipelineEntry(
                Context.CreateForClassMethod(info.getContractName(), info.getMethodName()),
                init, method.get().getBody()));
    }

    @Override
    protected void shutdown() throws ExecutionException {
        super.shutdown();
        this.transaction.endTransaction();
    }
}
