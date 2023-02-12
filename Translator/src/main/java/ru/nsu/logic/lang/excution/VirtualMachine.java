package ru.nsu.logic.lang.excution;

import ru.nsu.logic.lang.compilation.common.ICompilationRegistry;
import ru.nsu.logic.lang.compilation.common.ICompiledClass;
import ru.nsu.logic.lang.compilation.common.ICompiledFunction;
import ru.nsu.logic.lang.grammar.common.IStatement;
import ru.nsu.logic.lang.compilation.CompiledProgram;
import ru.nsu.logic.lang.excution.common.*;
import ru.nsu.logic.lang.grammar.VariableStatement;

import java.util.HashMap;


public class VirtualMachine implements IVirtualMachine {
    private final ICompilationRegistry<ICompiledClass> compiledClasses;
    private final ICompilationRegistry<ICompiledFunction> compiledFunctions;
    private final IScreen screen = new Screen();
    private final IPipeline pipeline = new Pipeline();

    public static VirtualMachine create(final CompiledProgram compiledProgram) {
        VirtualMachine machine = new VirtualMachine(
                compiledProgram.getCompiledClasses(),
                compiledProgram.getCompiledFunctions());

        machine.getPipeline().pushEntry(new PipelineEntry(new HashMap<>(), compiledProgram.getBody()));
        return machine;
    }

    private VirtualMachine(final ICompilationRegistry<ICompiledClass> compiledClasses,
                           final ICompilationRegistry<ICompiledFunction> compiledFunctions) {
        this.compiledClasses = compiledClasses;
        this.compiledFunctions = compiledFunctions;
    }

    @Override
    public ICompilationRegistry<ICompiledFunction> getCompiledFunctions() {
        return compiledFunctions;
    }

    @Override
    public ICompilationRegistry<ICompiledClass> getCompiledClasses() {
        return compiledClasses;
    }

    @Override
    public IScreen getScreen() {
        return screen;
    }

    @Override
    public IPipeline getPipeline() {
        return pipeline;
    }

    @Override
    public IStatement onPushEntry(final IPipelineEntry entry) {
        final String uniqueName = pipeline.getCurrentEntry().pushTempVariable();
        pipeline.pushEntry(entry);
        return new VariableStatement(uniqueName);
    }

    @Override
    public void onEntryCompleted(final IStatement result) {
        /// Remove entry from execution stack
        pipeline.popEntry();
        pipeline.getCurrentEntry().initializeVariable(
                pipeline.getCurrentEntry().popTempVariable(),
                result);
    }

    @Override
    public void run() throws ExecutionException {
        while (!pipeline.empty()) {
            final IPipelineEntry currentEntry = pipeline.getCurrentEntry();
            if (currentEntry.completed()) {
                pipeline.popEntry();
                continue;
            }
            final IStatement.ExecutionResult<IStatement> result = currentEntry.getCurrentStatement().execute(this);
            currentEntry.setCurrentStatement(result.getValue());
            if (result.isCompleted())
                currentEntry.nextStatement();
        }
    }
}
