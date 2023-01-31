package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.logic.lang.base.compilation.ICompiledFunction;
import ru.nsu.logic.lang.base.execution.ExecutionException;
import ru.nsu.logic.lang.base.execution.IPipelineEntry;
import ru.nsu.logic.lang.base.execution.IVirtualMachine;
import ru.nsu.logic.lang.base.grammar.IStatement;
import ru.nsu.logic.lang.builtins.common.BuiltinsRegistry;
import ru.nsu.logic.lang.excution.PipelineEntry;

import java.util.*;

public class FunctionCallStatement extends SimpleNode implements IStatement {
    @Getter
    @Setter
    private String functionName;

    @Setter
    @Getter
    private List<IStatement> callParameters;

    public FunctionCallStatement(int i) {
        super(i);
    }

    public FunctionCallStatement(LStatement p, int i) {
        super(p, i);
    }

    private FunctionCallStatement(final String functionName,
                                  final List<IStatement> callParameters) {
        super(GENERATED_STATEMENT_ID);
        this.functionName = functionName;
        this.callParameters = callParameters;
    }

    @Override
    public ExecutionResult execute(final IVirtualMachine machine) throws ExecutionException {
        if (!isBuiltInFunction() && callParameters.stream().allMatch(IStatement::executedInPlace))
            return placeToPipeline(machine);

        final List<IStatement> executed = new ArrayList<>(callParameters);
        for (int i = 0; i < callParameters.size(); ++i) {
            final IStatement parameter = callParameters.get(i);
            final boolean shouldBreak = !parameter.executedInPlace();

            executed.set(i, parameter.execute(machine).getStatement());
            if (shouldBreak)
                return new ExecutionResult(new FunctionCallStatement(functionName, executed), false);
        }
        /* Build-in, all args are calculated */
        final Optional<BuiltinsRegistry.BuiltinBuilder<?>> optional = BuiltinsRegistry.INSTANCE.lookup(functionName);
        assert(optional.isPresent());
        return new ExecutionResult(optional.get().build(machine).evaluate(executed), true);
    }

    @Override
    public boolean executedInPlace() {
        if (!isBuiltInFunction())
            return false;
        return callParameters.stream().allMatch(IStatement::executedInPlace);
    }

    private boolean isBuiltInFunction() {
        return BuiltinsRegistry.INSTANCE.lookup(functionName).isPresent();
    }

    private ExecutionResult placeToPipeline(final IVirtualMachine machine) throws ExecutionException {
        final Optional<ICompiledFunction> function = machine.getCompiledFunctions().lookup(functionName);
        if (!function.isPresent())
            throw new ExecutionException("Function not found");

        final List<String> argNames = function.get().getArguments();
        if (argNames.size() != callParameters.size())
            throw new ExecutionException(
                    "Wrong number of parameters in " + functionName + ". Expected: " + argNames.size());

        // Replace function call with variable statement and add new entry to pipeline
        final IPipelineEntry entry = new PipelineEntry(executeArguments(machine, argNames), function.get().getBody());
        return new ExecutionResult(machine.onPushEntry(entry), true);
    }

    private Map<String, IStatement> executeArguments(final IVirtualMachine machine,
                                                     final List<String> argNames) throws ExecutionException {
        final Map<String, IStatement> initializers = new HashMap<>();
        final Iterator<String> argumentIter = argNames.iterator();
        final Iterator<IStatement> stmtIter = callParameters.iterator();
        while (argumentIter.hasNext() && stmtIter.hasNext())
            initializers.put(argumentIter.next(), stmtIter.next().execute(machine).getStatement());
        return initializers;
    }
}
