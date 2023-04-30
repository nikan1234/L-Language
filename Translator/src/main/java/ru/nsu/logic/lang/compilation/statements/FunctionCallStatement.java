package ru.nsu.logic.lang.compilation.statements;

import lombok.*;
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
    @With(AccessLevel.PRIVATE)
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
                return uncompleted(withCallParameters(executed));
        }

        if (isBuiltInFunction()) {
            /// Build-in, all args are calculated /
            final Optional<BuiltinsRegistry.BuiltinBuilder<?>> optional = BuiltinsRegistry.INSTANCE.lookup(functionName);
            assert(optional.isPresent());
            return completed(optional.get().build(machine).evaluate(location, executed));
        }
        final IStatement retVal = machine.onPipelineExtend(withCallParameters(executed));
        return uncompleted(retVal);
    }

    private boolean isBuiltInFunction() {
        return BuiltinsRegistry.INSTANCE.lookup(functionName).isPresent();
    }
}
