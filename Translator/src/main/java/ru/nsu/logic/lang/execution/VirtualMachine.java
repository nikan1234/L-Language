package ru.nsu.logic.lang.execution;

import ru.nsu.logic.lang.compilation.common.ICompilationRegistry;
import ru.nsu.logic.lang.compilation.common.ICompiledClass;
import ru.nsu.logic.lang.compilation.common.ICompiledFunction;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.compilation.compiler.CompiledProgram;
import ru.nsu.logic.lang.compilation.statements.FunctionCallStatement;
import ru.nsu.logic.lang.compilation.statements.VariableStatement;
import ru.nsu.logic.lang.execution.common.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VirtualMachine implements IVirtualMachine {

    private final ICompilationRegistry<ICompiledClass> compiledClasses;
    private final ICompilationRegistry<ICompiledFunction> compiledFunctions;
    private final IScreen screen = new Screen();
    private final IPipeline pipeline = new Pipeline();

    public static VirtualMachine create(final CompiledProgram compiledProgram) {
        final VirtualMachine machine = new VirtualMachine(
                compiledProgram.getCompiledClasses(),
                compiledProgram.getCompiledFunctions());

        machine.getPipeline().pushEntry(new PipelineEntry("", new HashMap<>(), compiledProgram.getStatements()));
        return machine;
    }

    private VirtualMachine(final ICompilationRegistry<ICompiledClass> compiledClasses,
                           final ICompilationRegistry<ICompiledFunction> compiledFunctions) {
        this.compiledClasses = compiledClasses;
        this.compiledFunctions = compiledFunctions;
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
    public void run() throws ExecutionException {
        IContext context = null;
        try {
            while (!pipeline.empty()) {
                final IPipelineEntry currentEntry = pipeline.getCurrentEntry();
                if (currentEntry.completed()) {
                    pipeline.popEntry();
                    continue;
                }
                context = pipeline.getContext();
                final IStatement statement = currentEntry.getCurrentStatement();
                final IStatement.ExecutionResult<IStatement> result = statement.execute(this);
                currentEntry.setCurrentStatement(result.getValue());
                if (result.isCompleted())
                    currentEntry.nextStatement();
            }
        }
        catch (final ExecutionException e) {
            if (context != null)
                throw new ExecutionException(
                        "Error at " + context.getLocation().getRow() +
                                ":" + context.getLocation().getColumn() +
                                ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public IStatement onPipelineExtend(final IStatement statement) throws ExecutionException {
        if (!(statement instanceof FunctionCallStatement))
            throw new RuntimeException("Internal execution error");

        final FunctionCallStatement callStmt = (FunctionCallStatement) statement;
        final String functionName = callStmt.getFunctionName();
        final IContext context = pipeline.getContext();

        if (functionName.equals(context.getFunctionName()))
            throw new ExecutionException("Found recursion in " + callStmt.getFunctionName());

        final Optional<ICompiledFunction> function = compiledFunctions.lookup(functionName);
        if (!function.isPresent())
            throw new ExecutionException("Function not found: " + functionName);
        if (function.get().getLocation().compareTo(context.getLocation()) > 0)
            throw new ExecutionException("Function " + functionName + " is not declared yet");

        final List<String> argNames = function.get().getArguments();
        final List<IStatement> callParameters = callStmt.getCallParameters();
        if (argNames.size() != callStmt.getCallParameters().size())
            throw new ExecutionException(
                    "Wrong number of parameters in " + functionName + ". Expected: " + argNames.size());

        /// Replace function call with variable statement and add new entry to pipeline
        final IPipelineEntry entry = new PipelineEntry(
                functionName,
                IntStream.range(0, argNames.size()).boxed()
                        .collect(Collectors.toMap(argNames::get, callParameters::get)),
                function.get().getBody()
        );

        /// False if function result is not assigned to any var
        boolean shouldCreateTempVar = pipeline.getCurrentEntry().getCurrentStatement() != statement;

        final String uniqueName = shouldCreateTempVar ? pipeline.getCurrentEntry().pushTempVariable() : null;
        pipeline.pushEntry(entry);
        return shouldCreateTempVar ? new VariableStatement(uniqueName, statement.getLocation()) : null;
    }

    @Override
    public void onPipelineRollback(final IStatement statement) {
        /// Remove entry from execution stack
        pipeline.popEntry();
        if (pipeline.getCurrentEntry().hasTempVariable())
            pipeline.getCurrentEntry().initializeVariable(
                    pipeline.getCurrentEntry().popTempVariable(),
                    statement);
    }
}
