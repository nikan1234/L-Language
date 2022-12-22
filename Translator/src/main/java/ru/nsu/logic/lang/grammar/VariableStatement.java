package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.logic.lang.base.execution.ExecutionException;
import ru.nsu.logic.lang.base.execution.IVirtualMachine;
import ru.nsu.logic.lang.base.grammar.IStatement;

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
    public ExecutionResult execute(IVirtualMachine machine) throws ExecutionException {
        return new ExecutionResult(
                machine.getPipeline().getCurrentEntry().getInitializedVariable(name),
                true);
    }

    @Override
    public boolean executedInPlace() {
        return true;
    }
}
