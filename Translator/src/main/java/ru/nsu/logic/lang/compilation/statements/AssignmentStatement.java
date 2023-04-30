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

    @Getter
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
            return uncompleted(new AssignmentStatement(target, value, getLocation()));

        if (target instanceof VariableStatement) {
            ((VariableStatement) target).setValue(machine, value);
            return completed(null);
        }

        if (target instanceof MemberStatement) {
            ((MemberStatement) target).setValue(machine, value);
            return completed(null);
        }

        throw new ExecutionException("Cannot assign to " + target);
    }
}
