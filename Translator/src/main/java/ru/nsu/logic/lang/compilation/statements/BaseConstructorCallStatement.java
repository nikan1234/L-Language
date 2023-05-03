package ru.nsu.logic.lang.compilation.statements;

import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.common.AccessType;
import ru.nsu.logic.lang.compilation.common.ICompiledClass;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IContext;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class BaseConstructorCallStatement extends ConstructorCallStatement implements IStatement {

    public BaseConstructorCallStatement(final FileLocation location) {
        this(new ArrayList<>(), location);
    }

    public BaseConstructorCallStatement(final List<IStatement> callParameters, final FileLocation location) {
        super("super", callParameters, location);
    }

    @Override
    public ExecutionResult<IStatement> execute(final IVirtualMachine machine) throws ExecutionException {
        final IContext ctx = machine.getPipeline().getCurrentContext();
        if (!ctx.isClassMethodCtx() ||
            !ICompiledClass.CTOR_NAME.equals(ctx.getClassMethodCtx().getMethodName()))
            throw new ExecutionException("Cannot call base constructor outside constructor");

        /// Evaluate arguments
        final List<IStatement> executed = new ArrayList<>(getCallParameters());
        for (int i = 0; i < executed.size(); ++i) {
            final IStatement param = executed.get(i);
            final ExecutionResult<IStatement> executionResult = param.execute(machine);

            executed.set(i, executionResult.getValue());
            if (!executionResult.isCompleted())
                return uncompleted(new BaseConstructorCallStatement(executed, getLocation()));
        }

        final IStatement retVal = machine.onPipelineExtend(new BaseConstructorCallStatement(executed, getLocation()));
        return new ExecutionResult<>(retVal, retVal instanceof ObjectValueStatement);
    }

    public EnumSet<AccessType> getAccessMask() {
        return AccessType.Masks.PUBLIC_AND_PROTECTED;
    }
}
