package ru.nsu.logic.lang.grammar;

import lombok.Setter;
import ru.nsu.logic.lang.excution.common.ExecutionException;
import ru.nsu.logic.lang.excution.common.IVirtualMachine;
import ru.nsu.logic.lang.grammar.common.IStatement;

public class AssignmentStatement extends SimpleNode implements IStatement {
    @Setter
    private IStatement target;

    @Setter
    private IStatement what;

    public AssignmentStatement(int i) {
        super(i);
    }

    private AssignmentStatement(final IStatement target, final IStatement what) {
        this.target = target;
        this.what = what;
    }

    @Override
    public ExecutionResult<IStatement> execute(final IVirtualMachine machine) throws ExecutionException {
        final ExecutionResult<IStatement> whatExecuted =  what.execute(machine);
        final IStatement value = whatExecuted.getValue();
        if (!whatExecuted.isCompleted())
            return new ExecutionResult<>(new AssignmentStatement(target, value), false);

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
