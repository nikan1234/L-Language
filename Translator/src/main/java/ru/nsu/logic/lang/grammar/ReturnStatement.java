package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.logic.lang.base.execution.ExecutionException;
import ru.nsu.logic.lang.base.execution.IVirtualMachine;
import ru.nsu.logic.lang.base.grammar.IStatement;

public class ReturnStatement extends SimpleNode implements IStatement {
    @Setter
    @Getter
    private IStatement what;

    public ReturnStatement(int i) {
        super(i);
    }

    public ReturnStatement(LStatement p, int i) {
        super(p, i);
    }

    private ReturnStatement(final IStatement what) {
        super(GENERATED_STATEMENT_ID);
        this.what = what;
    }

    @Override
    public ExecutionResult execute(final IVirtualMachine machine) throws ExecutionException {
        final IStatement result = what.execute(machine).getStatement();
        if (what.executedInPlace()) {
            machine.onEntryCompleted(result);
            return new ExecutionResult(null, true);
        }
        return new ExecutionResult(new ReturnStatement(result), false);
    }

    @Override
    public boolean executedInPlace() {
        return what.executedInPlace();
    }
}
