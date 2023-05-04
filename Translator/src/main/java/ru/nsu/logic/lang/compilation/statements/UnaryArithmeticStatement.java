package ru.nsu.logic.lang.compilation.statements;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.common.ArithmeticOperator;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

@AllArgsConstructor
public class UnaryArithmeticStatement implements IStatement {
    @With(AccessLevel.PRIVATE)
    private IStatement operand;
    private ArithmeticOperator operator;

    @With
    @Getter
    private final FileLocation location;

    @Override
    public ExecutionResult<IStatement> execute(IVirtualMachine machine) throws ExecutionException {
        if (operator != ArithmeticOperator.SUB)
            throw new ExecutionException("Unsupported unary operator");

        final ExecutionResult<IStatement> operandExecuted = operand.execute(machine);
        if (!operandExecuted.isCompleted())
            return uncompleted(withOperand(operandExecuted.getValue()));

        if (!(operandExecuted.getValue() instanceof NumberValueStatement))
            throw new ExecutionException("Expected number");

        final NumberValueStatement number = (NumberValueStatement) operandExecuted.getValue();
        return completed(number.negate().withLocation(location));
    }
}
