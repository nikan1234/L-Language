package ru.nsu.logic.lang.compilation.statements.logic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.common.LogicOperator;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

@AllArgsConstructor
public class BinaryLogicStatement implements IFormula {
    private IFormula lhs;
    private IFormula rhs;
    private LogicOperator operator;

    @With
    @Getter
    private final FileLocation location;

    @Override
    public ExecutionResult<IFormula> execute(final IVirtualMachine machine) throws ExecutionException {
        final ExecutionResult<IFormula> lhsExecuted = lhs.execute(machine);
        if (!lhsExecuted.isCompleted())
            return uncompleted(new BinaryLogicStatement(lhsExecuted.getValue(), rhs, operator, location));

        if (operator == LogicOperator.OR && asBool(lhsExecuted.getValue()))
            return completed(new BooleanValueStatement(true));
        if (operator == LogicOperator.AND && !asBool(lhsExecuted.getValue()))
            return completed(new BooleanValueStatement(false));

        final ExecutionResult<IFormula> rhsExecuted = rhs.execute(machine);
        if (!rhsExecuted.isCompleted())
            return uncompleted(new BinaryLogicStatement(lhsExecuted.getValue(), rhsExecuted.getValue(),
                    operator, location));

        boolean result;
        switch (operator) {
            case AND:
                result = asBool(lhsExecuted.getValue()) && asBool(rhsExecuted.getValue());
                break;
            case OR:
                result = asBool(lhsExecuted.getValue()) || asBool(rhsExecuted.getValue());
                break;
            default:
                throw new ExecutionException("Unsupported binary operator: " + operator);
        }
        return completed(new BooleanValueStatement(result));
    }

    private static boolean asBool(final IFormula formula) throws ExecutionException {
        if (!(formula instanceof BooleanValueStatement))
            throw new ExecutionException("Expected boolean");

        return ((BooleanValueStatement) formula).getValue();
    }
}
