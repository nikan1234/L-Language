package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.logic.lang.base.IStatement;

import java.util.List;

public class ComplexStatement extends SimpleNode implements IStatement {
    @Setter
    @Getter
    private List<IStatement> operands;

    @Setter
    @Getter
    private List<String> operators;

    public ComplexStatement(int i) {
        super(i);
    }

    public ComplexStatement(LStatement p, int i) {
        super(p, i);
    }

    @Override
    public boolean executedInPlace() {
        return true;
    }
}
