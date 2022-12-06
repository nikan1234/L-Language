package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.logic.lang.base.IStatement;


public class FloatValue extends SimpleNode implements IStatement {
    @Getter
    @Setter
    private double value;


    public FloatValue(int i) {
        super(i);
    }

    public FloatValue(LStatement p, int i) {
        super(p, i);
    }

    @Override
    public boolean executedInPlace() {
        return true;
    }
}

