package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.logic.lang.base.IStatement;

public class IntegerValue extends SimpleNode implements IStatement {
    @Getter
    @Setter
    private long value;


    public IntegerValue(int i) {
        super(i);
    }

    public IntegerValue(LStatement p, int i) {
        super(p, i);
    }

    @Override
    public boolean executedInPlace() {
        return true;
    }
}
