package ru.nsu.logic.lang.grammar;

import lombok.Setter;
import ru.nsu.logic.lang.base.execution.ExecutionException;
import ru.nsu.logic.lang.base.execution.IVirtualMachine;
import ru.nsu.logic.lang.base.grammar.IStatement;

public class AssignmentStatement extends SimpleNode implements IStatement {
    @Setter
    private IStatement target;

    @Setter
    private IStatement what;

    public AssignmentStatement(int i) {
        super(i);
    }

    public AssignmentStatement(LStatement p, int i) {
        super(p, i);
    }

    private AssignmentStatement(final IStatement target, final IStatement what) {
        super(GENERATED_STATEMENT_ID);
        this.target = target;
        this.what = what;
    }

    @Override
    public ExecutionResult execute(IVirtualMachine machine) throws ExecutionException {
        if (!what.executedInPlace())
            return new ExecutionResult(
                    new AssignmentStatement(target, what.execute(machine).getStatement()),
                    false);

        final IStatement value =  what.execute(machine).getStatement();
        if (target instanceof VariableStatement) {
            VariableStatement variable = (VariableStatement) target;
            machine.getPipeline().getCurrentEntry().initializeVariable(variable.getName(), value);
            return new ExecutionResult(null, true);
        }
        if (target instanceof MemberStatement) {
            MemberStatement member = (MemberStatement) target;
            return new ExecutionResult(null, true);
        }
        throw new ExecutionException("Cannot assign to " + target);
    }

    @Override
    public boolean executedInPlace() {
        return true;
    }
}
