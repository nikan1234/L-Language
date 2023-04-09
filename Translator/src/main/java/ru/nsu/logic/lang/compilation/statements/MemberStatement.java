package ru.nsu.logic.lang.compilation.statements;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.common.AccessType;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IContext;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

import java.util.EnumSet;


@AllArgsConstructor
public class MemberStatement implements IStatement {
    @Getter
    private String objectName;
    @Getter
    private String memberName;
    @With
    @Getter
    private final FileLocation location;

    void setValue(final IVirtualMachine machine, final IStatement statement) throws ExecutionException {
        getObject(machine).setMemberValue(memberName, statement, getAccessMask(machine));
    }

    @Override
    public ExecutionResult<IStatement> execute(IVirtualMachine machine) throws ExecutionException {
        return new ExecutionResult<>(getObject(machine).getMemberValue(memberName, getAccessMask(machine)), true);
    }

    private EnumSet<AccessType> getAccessMask(final IVirtualMachine machine) throws ExecutionException {
        final IContext context = machine.getPipeline().getCurrentContext();
        if ("this".equals(objectName) && !context.isClassMethodCtx())
            throw new ExecutionException("This can be used only inside class");

        return "this".equals(objectName) ? AccessType.Masks.ALL : AccessType.Masks.ONLY_PUBLIC;
    }

    private ObjectValue getObject(final IVirtualMachine machine) throws ExecutionException {
        final IStatement object = machine.getPipeline().getCurrentEntry().getInitializedVariable(objectName);
        if (!(object instanceof ObjectValue))
            throw new RuntimeException(objectName + " refers to an non-object: " + object);

        return (ObjectValue) object;
    }
}
