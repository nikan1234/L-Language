package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.logic.lang.base.execution.ExecutionException;
import ru.nsu.logic.lang.base.execution.IVirtualMachine;
import ru.nsu.logic.lang.base.grammar.IStatement;
import ru.nsu.logic.lang.utils.Calculator;

import java.util.ArrayList;
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

    private ComplexStatement(final List<IStatement> operands,
                             final List<String> operators) {
        super(GENERATED_STATEMENT_ID);
        this.operands = operands;
        this.operators = operators;
    }

    @Override
    public ExecutionResult execute(IVirtualMachine machine) throws ExecutionException {
        if (operators.isEmpty())
            throw new ExecutionException("No operators found");

        final List<IStatement> executed = new ArrayList<>(operands);
        for (int i = 0; i < operands.size(); ++i) {
            final IStatement operand = operands.get(i);
            final boolean shouldBreak = !operand.executedInPlace();

            executed.set(i, operand.execute(machine).getStatement());
            if (shouldBreak)
                return new ExecutionResult(new ComplexStatement(executed, operators), false);
        }
        return new ExecutionResult(new Calculator(executed, operators).calculate(), true);
    }

    @Override
    public boolean executedInPlace() {
        return operands.stream().allMatch(IStatement::executedInPlace);
    }
}
