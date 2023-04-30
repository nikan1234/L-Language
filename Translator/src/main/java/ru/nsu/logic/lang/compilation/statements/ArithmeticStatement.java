package ru.nsu.logic.lang.compilation.statements;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;
import ru.nsu.logic.lang.utils.ArithmeticEvaluator;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ArithmeticStatement implements IStatement {

    @With(AccessLevel.PRIVATE)
    private List<IStatement> operands;
    private List<String> operators;

    @With
    @Getter
    private final FileLocation location;


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
                return uncompleted(withOperands(executed));
        }
        return completed(new ArithmeticEvaluator(executed, operators, location).calculate());
    }
}
