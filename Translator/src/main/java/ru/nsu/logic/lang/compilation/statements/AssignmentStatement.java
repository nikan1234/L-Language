package ru.nsu.logic.lang.compilation.statements;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

@AllArgsConstructor
public class AssignmentStatement implements IStatement {

    private IStatement target;
    private IStatement what;

    @With
    @Getter
    private final FileLocation location;

    @Override
    public ExecutionResult<IStatement> execute(final IVirtualMachine machine) throws ExecutionException {
        final ExecutionResult<IStatement> whatExecuted =  what.execute(machine);
        final IStatement value = whatExecuted.getValue();
        if (!whatExecuted.isCompleted())
            return new ExecutionResult<>(new AssignmentStatement(target, value, getLocation()), false);

        if (target instanceof VariableStatement) {
            final VariableStatement variable = (VariableStatement) target;
            machine.getPipeline().getCurrentEntry().initializeVariable(variable.getName(), value);
            return new ExecutionResult<>(null, true);
        }
        if (target instanceof MemberStatement) {
            MemberStatement member = (MemberStatement) target;
            return new ExecutionResult<>(null, true);
        }
        throw new ExecutionException("Cannot assign to " + target);
    }
}
