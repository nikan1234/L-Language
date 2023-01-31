package ru.nsu.logic.lang.excution;

import ru.nsu.logic.lang.base.compilation.ICompilationRegistry;
import ru.nsu.logic.lang.base.compilation.ICompiledClass;
import ru.nsu.logic.lang.base.compilation.ICompiledFunction;
import ru.nsu.logic.lang.base.execution.*;
import ru.nsu.logic.lang.base.grammar.IStatement;
import ru.nsu.logic.lang.compilator.CompiledProgram;
import ru.nsu.logic.lang.grammar.VariableStatement;

import java.util.HashMap;

import static ru.nsu.logic.lang.base.grammar.IStatement.GENERATED_STATEMENT_ID;


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

        final VariableStatement stmt = new VariableStatement(GENERATED_STATEMENT_ID);
        stmt.setName(uniqueName);
        return stmt;
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
            final IStatement.ExecutionResult result = currentEntry.getCurrentStatement().execute(this);
            currentEntry.setCurrentStatement(result.getStatement());
            if (result.isCompleted())
                currentEntry.nextStatement();
        }
    }
}
