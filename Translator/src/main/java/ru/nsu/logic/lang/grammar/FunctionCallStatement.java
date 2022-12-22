package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.logic.lang.base.compilation.ICompiledFunction;
import ru.nsu.logic.lang.base.execution.ExecutionException;
import ru.nsu.logic.lang.base.execution.IVirtualMachine;
import ru.nsu.logic.lang.base.grammar.IStatement;
import ru.nsu.logic.lang.excution.PipelineEntry;
import ru.nsu.logic.lang.utils.ListUtils;

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
    public ExecutionResult execute(IVirtualMachine machine) throws ExecutionException {
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
        /* Build-it, all args are calculated */
        return new ExecutionResult(null, false);
    }

    @Override
    public boolean executedInPlace() {
        if (!isBuiltInFunction())
            return false;
        return callParameters.stream().allMatch(IStatement::executedInPlace);
    }

    private boolean isBuiltInFunction() {
        return false;
    }

    private ExecutionResult placeToPipeline(IVirtualMachine machine) throws ExecutionException {
        final VariableStatement stmt = new VariableStatement(GENERATED_STATEMENT_ID);
        stmt.setName(machine.getPipeline().getCurrentEntry().addUniqueTemporaryVariable());

        final Optional<ICompiledFunction> function = machine.getCompiledFunctions().lookup(functionName);
        if (!function.isPresent())
            throw new ExecutionException("Function not found");

        final List<String> argNames = function.get().getArguments();
        if (argNames.size() != callParameters.size())
            throw new ExecutionException(
                    "Wrong number of parameters in " + functionName + ". Expected: " + argNames.size());

        final Map<String, IStatement> initializers = new HashMap<>();
        final Iterator<String> argumentIter = argNames.iterator();
        final Iterator<IStatement> stmtIter = callParameters.iterator();
        while (argumentIter.hasNext() && stmtIter.hasNext())
            initializers.put(argumentIter.next(), stmtIter.next().execute(machine).getStatement());

        PipelineEntry newEntry = new PipelineEntry(initializers, function.get().getBody());
        machine.getPipeline().pushEntry(newEntry);
        return new ExecutionResult(stmt, true);
    }
}
