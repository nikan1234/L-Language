package ru.nsu.logic.lang.compilation.statements;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.builtins.common.BuiltinsRegistry;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class FunctionCallStatement implements IStatement {
    @Getter
    private String functionName;
    @Getter
    private List<IStatement> callParameters;
    @With
    @Getter
    private final FileLocation location;

    @Override
    public ExecutionResult<IStatement> execute(final IVirtualMachine machine) throws ExecutionException {

        /// Evaluate arguments
        final List<IStatement> executed = new ArrayList<>(callParameters);
        for (int i = 0; i < callParameters.size(); ++i) {
            final IStatement param = callParameters.get(i);
            final ExecutionResult<IStatement> executionResult = param.execute(machine);

            executed.set(i, executionResult.getValue());
            if (!executionResult.isCompleted())
                return new ExecutionResult<>(
                        new FunctionCallStatement(functionName, executed, getLocation()),
                        false);
        }

        if (isBuiltInFunction()) {
            /// Build-in, all args are calculated /
            final Optional<BuiltinsRegistry.BuiltinBuilder<?>> optional = BuiltinsRegistry.INSTANCE.lookup(functionName);
            assert(optional.isPresent());
            return new ExecutionResult<>(optional.get().build(machine).evaluate(location, executed), true);
        }
        return new ExecutionResult<>(machine.onPipelineExtend(
                new FunctionCallStatement(functionName, executed, getLocation())), false);
    }

    private boolean isBuiltInFunction() {
        return BuiltinsRegistry.INSTANCE.lookup(functionName).isPresent();
    }
}
