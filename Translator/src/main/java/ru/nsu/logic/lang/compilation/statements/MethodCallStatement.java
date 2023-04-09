package ru.nsu.logic.lang.compilation.statements;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.With;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.common.AccessType;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IContext;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@EqualsAndHashCode
@AllArgsConstructor
public class MethodCallStatement implements IStatement {
    @Getter
    private String objectName;
    @Getter
    private String methodName;
    @Getter
    @EqualsAndHashCode.Exclude
    private List<IStatement> callParameters;
    @With
    @Getter
    private final FileLocation location;

    @Override
    public ExecutionResult<IStatement> execute(final IVirtualMachine machine) throws ExecutionException {

        final IContext context = machine.getPipeline().getCurrentContext();
        if ("this".equals(objectName) && !context.isClassMethodCtx())
            throw new ExecutionException("Cannot use this outside of class");

        /// Evaluate arguments
        final List<IStatement> executed = new ArrayList<>(callParameters);
        for (int i = 0; i < callParameters.size(); ++i) {
            final IStatement param = callParameters.get(i);
            final ExecutionResult<IStatement> executionResult = param.execute(machine);

            executed.set(i, executionResult.getValue());
            if (!executionResult.isCompleted())
                return new ExecutionResult<>(
                        new MethodCallStatement(objectName, methodName, executed, getLocation()),
                        false);
        }

        final IStatement retVal = machine.onPipelineExtend(
                new MethodCallStatement(objectName, methodName, executed, getLocation()));
        return new ExecutionResult<>(retVal, retVal == null);
    }

    public EnumSet<AccessType> getAccessMask() {
        return "this".equals(objectName) ? AccessType.Masks.ALL : AccessType.Masks.ONLY_PUBLIC;
    }
}
