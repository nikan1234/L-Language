package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.logic.lang.base.IStatement;

public class MemberStatement extends SimpleNode implements IStatement {
    @Getter
    @Setter
    private String name;

    public MemberStatement(int i) {
        super(i);
    }

    public MemberStatement(LStatement p, int i) {
        super(p, i);
    }

    @Override
    public boolean executedInPlace() {
        return true;
    }
}
