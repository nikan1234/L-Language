package ru.nsu.logic.lang.compilation.statements.logic;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.common.LogicOperator;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

@AllArgsConstructor
public class UnaryLogicStatement implements IFormula {
    @With(AccessLevel.PRIVATE)
    private IFormula operand;
    private LogicOperator operator;

    @With
    @Getter
    private final FileLocation location;

    @Override
    public ExecutionResult<IFormula> execute(IVirtualMachine machine) throws ExecutionException {
        if (operator != LogicOperator.NOT)
            throw new ExecutionException("Unsupported unary operator");

        final ExecutionResult<IFormula> operandExecuted = operand.execute(machine);
        if (!operandExecuted.isCompleted())
            return uncompleted(withOperand(operandExecuted.getValue()));

        if (!(operandExecuted.getValue() instanceof BooleanValueStatement))
            throw new ExecutionException("Expected boolean");

        final BooleanValueStatement bool = (BooleanValueStatement) operandExecuted.getValue();
        return completed(new BooleanValueStatement(!bool.getValue()));
    }
}
