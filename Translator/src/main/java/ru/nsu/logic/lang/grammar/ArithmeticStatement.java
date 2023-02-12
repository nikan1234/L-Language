package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.logic.lang.excution.common.ExecutionException;
import ru.nsu.logic.lang.excution.common.IVirtualMachine;
import ru.nsu.logic.lang.grammar.common.IStatement;
import ru.nsu.logic.lang.utils.ArithmeticCalculator;

import java.util.ArrayList;
import java.util.List;

public class ArithmeticStatement extends SimpleNode implements IStatement {
    @Setter
    @Getter
    private List<IStatement> operands;

    @Setter
    @Getter
    private List<String> operators;

    public ArithmeticStatement(final int i) {
        super(i);
    }

    private ArithmeticStatement(final List<IStatement> operands,
                                final List<String> operators) {
        this.operands = operands;
        this.operators = operators;
    }

    @Override
    public ExecutionResult<IStatement> execute(final IVirtualMachine machine) throws ExecutionException {
        if (operators.isEmpty())
            throw new ExecutionException("No operators found");

        final List<IStatement> executed = new ArrayList<>(operands);
        for (int i = 0; i < operands.size(); ++i) {
            final IStatement operand = operands.get(i);
            final ExecutionResult<IStatement> executionResult = operand.execute(machine);

            executed.set(i, executionResult.getValue());
            if (!executionResult.isCompleted())
                return new ExecutionResult<>(new ArithmeticStatement(executed, operators), false);
        }
        return new ExecutionResult<>(new ArithmeticCalculator(executed, operators).calculate(), true);
    }
}
