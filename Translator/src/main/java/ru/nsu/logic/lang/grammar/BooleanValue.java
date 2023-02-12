package ru.nsu.logic.lang.grammar;

import ru.nsu.logic.lang.excution.common.ExecutionException;
import ru.nsu.logic.lang.excution.common.IVirtualMachine;
import ru.nsu.logic.lang.grammar.common.IStatement;

public class BooleanValue implements IStatement {
    private final boolean value;

    public BooleanValue(final boolean value) {
        this.value = value;
    }

    @Override
    public ExecutionResult execute(IVirtualMachine machine) throws ExecutionException {
        return new ExecutionResult(this, true);
    }

    @Override
    public boolean executedInPlace() {
        return true;
    }

    public boolean isTrue() {
        return value;
    }
}
