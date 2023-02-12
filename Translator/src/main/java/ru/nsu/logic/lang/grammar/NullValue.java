package ru.nsu.logic.lang.grammar;

import ru.nsu.logic.lang.excution.common.ExecutionException;
import ru.nsu.logic.lang.excution.common.IVirtualMachine;
import ru.nsu.logic.lang.grammar.common.IStatement;

public class NullValue extends SimpleNode implements IStatement {

    public NullValue(int i) {
        super(i);
    }

    @Override
    public ExecutionResult<IStatement> execute(IVirtualMachine machine) throws ExecutionException {
        return new ExecutionResult<>(this, true);
    }

    @Override
    public String toString() {
        return "nil";
    }
}
