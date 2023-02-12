package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.logic.lang.excution.common.ExecutionException;
import ru.nsu.logic.lang.excution.common.IVirtualMachine;
import ru.nsu.logic.lang.grammar.common.IStatement;

public class NumberValue extends SimpleNode implements IStatement {
    @Getter
    @Setter
    private Number number;

    public NumberValue() {}

    public NumberValue(int i) {
        super(i);
    }

    public double asDouble() {
        return number.intValue();
    }

    public long asInt() throws ExecutionException {
        if (number instanceof Long)
            return number.longValue();
        throw new ExecutionException("Expected integer");
    }

    @Override
    public ExecutionResult<IStatement> execute(IVirtualMachine machine) {
        return new ExecutionResult<>(this, true);
    }


    @Override
    public String toString() {
        return number.toString();
    }
}
