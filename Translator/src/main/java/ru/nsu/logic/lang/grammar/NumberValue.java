package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.logic.lang.base.execution.IVirtualMachine;
import ru.nsu.logic.lang.base.grammar.IStatement;

public class NumberValue extends SimpleNode implements IStatement {
    @Getter
    @Setter
    private Number number;


    public NumberValue(int i) {
        super(i);
    }

    public NumberValue(LStatement p, int i) {
        super(p, i);
    }

    @Override
    public ExecutionResult execute(IVirtualMachine machine) {
        return new ExecutionResult(this, true);
    }

    @Override
    public boolean executedInPlace() {
        return true;
    }

    @Override
    public String toString() {
        return number.toString();
    }
}
