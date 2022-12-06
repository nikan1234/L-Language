package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.logic.lang.base.IStatement;

import java.util.List;

public class FunctionCallStatement extends SimpleNode implements IStatement {
    @Getter
    @Setter
    private String functionName;

    @Setter
    @Getter
    private List<IStatement> callParameters;

    public FunctionCallStatement(int i) {
        super(i);
    }

    public FunctionCallStatement(LStatement p, int i) {
        super(p, i);
    }

    @Override
    public boolean executedInPlace() {
        //TODO
        return true;
    }
}
