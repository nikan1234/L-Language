package ru.nsu.logic.lang.compilation.statements;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.common.AccessType;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@AllArgsConstructor
public class ConstructorCallStatement implements IStatement {
    @Getter
    private String className;
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

        final IStatement retVal = machine.onPipelineExtend(this);
        return new ExecutionResult<>(retVal, retVal instanceof ObjectValueStatement);
    }

    public EnumSet<AccessType> getAccessMask() {
        return AccessType.Masks.ONLY_PUBLIC;
    }
}
