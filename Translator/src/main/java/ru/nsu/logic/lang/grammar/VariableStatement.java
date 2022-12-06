package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.logic.lang.base.IStatement;

public class VariableStatement extends SimpleNode implements IStatement {
    @Getter
    @Setter
    private String name;

    public VariableStatement(int i) {
        super(i);
    }

    public VariableStatement(LStatement p, int i) {
        super(p, i);
    }

    @Override
    public boolean executedInPlace() {
        return true;
    }
}
