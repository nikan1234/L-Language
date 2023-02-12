package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.logic.lang.excution.common.IVirtualMachine;
import ru.nsu.logic.lang.grammar.common.IStatement;

public class MemberStatement extends SimpleNode implements IStatement {
    @Getter
    @Setter
    private String name;

    public MemberStatement(int i) {
        super(i);
    }

    @Override
    public ExecutionResult execute(IVirtualMachine machine) {
        return null;
    }

    @Override
    public boolean executedInPlace() {
        return true;
    }
}
