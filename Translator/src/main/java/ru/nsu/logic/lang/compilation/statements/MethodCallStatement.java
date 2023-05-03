package ru.nsu.logic.lang.compilation.statements;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.common.AccessType;
import ru.nsu.logic.lang.compilation.common.IObject;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IContext;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@AllArgsConstructor
public class MethodCallStatement implements IStatement {
    @Getter
    private String objectName;
    @Getter
    private String methodName;
    @Getter
    @With(AccessLevel.PRIVATE)
    private List<IStatement> callParameters;
    @With
    @Getter
    private final FileLocation location;

    @Override
    public ExecutionResult<IStatement> execute(final IVirtualMachine machine) throws ExecutionException {

        final IContext context = machine.getPipeline().getCurrentContext();
        if (isSelfMethodCall() && !context.isClassMethodCtx())
            throw new ExecutionException("Cannot use this outside of class");

        /// Evaluate arguments
        final List<IStatement> executed = new ArrayList<>(callParameters);
        for (int i = 0; i < callParameters.size(); ++i) {
            final IStatement param = callParameters.get(i);
            final ExecutionResult<IStatement> executionResult = param.execute(machine);

            executed.set(i, executionResult.getValue());
            if (!executionResult.isCompleted())
                return uncompleted(withCallParameters(executed));
        }
        return uncompleted(machine.onPipelineExtend(withCallParameters(executed)));
    }

    public IObject getObject(final IVirtualMachine machine) throws ExecutionException {
        if ("super".equals(objectName))
            return getObjectImpl(machine, "this").toBase();

        return getObjectImpl(machine, objectName);
    }

    public EnumSet<AccessType> getAccessMask() {
        if ("this".equals(objectName))
            return AccessType.Masks.ALL;
        if ("super".equals(objectName))
            return AccessType.Masks.PUBLIC_AND_PROTECTED;
        return AccessType.Masks.ONLY_PUBLIC;
    }

    private boolean isSelfMethodCall() {
        return "this".equals(objectName) || "super".equals(objectName);
    }

    private static IObject getObjectImpl(final IVirtualMachine machine,
                                         final String objectName) throws ExecutionException {
        final IStatement object = machine.getPipeline().getCurrentEntry().getInitializedVariable(objectName);
        if (!(object instanceof IObject))
            throw new ExecutionException(objectName + " refers to an non-object: " + object);

        return (IObject) object;
    }
}
