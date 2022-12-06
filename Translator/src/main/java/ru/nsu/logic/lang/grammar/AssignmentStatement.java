package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.logic.lang.base.IStatement;

public class AssignmentStatement extends SimpleNode implements IStatement {
    @Getter
    @Setter
    private IStatement target;

    @Setter
    @Getter
    private IStatement what;

    public AssignmentStatement(int i) {
        super(i);
    }

    public AssignmentStatement(LStatement p, int i) {
        super(p, i);
    }

    @Override
    public boolean executedInPlace() {
        return true;
    }
}
