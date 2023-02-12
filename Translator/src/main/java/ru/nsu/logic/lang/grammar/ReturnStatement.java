package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.logic.lang.excution.common.ExecutionException;
import ru.nsu.logic.lang.excution.common.IVirtualMachine;
import ru.nsu.logic.lang.grammar.common.IStatement;

public class ReturnStatement extends SimpleNode implements IStatement {
    @Setter
    @Getter
    private IStatement what;

    public ReturnStatement(int i) {
        super(i);
    }

    private ReturnStatement(final IStatement what) {
        this.what = what;
    }

    @Override
    public ExecutionResult<IStatement> execute(final IVirtualMachine machine) throws ExecutionException {
        final ExecutionResult<IStatement> whatExecuted = what.execute(machine);
        if (whatExecuted.isCompleted()) {
            machine.onEntryCompleted(whatExecuted.getValue());
            return new ExecutionResult<>(null, true);
        }
        return new ExecutionResult<>(new ReturnStatement(whatExecuted.getValue()), false);
    }
}
