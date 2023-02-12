package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.logic.lang.excution.common.ExecutionException;
import ru.nsu.logic.lang.excution.common.IVirtualMachine;
import ru.nsu.logic.lang.grammar.common.IStatement;

public class VariableStatement extends SimpleNode implements IStatement {
    @Getter
    @Setter
    private String name;

    public VariableStatement(int i) {
        super(i);
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
