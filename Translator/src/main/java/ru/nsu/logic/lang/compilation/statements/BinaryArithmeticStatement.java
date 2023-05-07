package ru.nsu.logic.lang.compilation.statements;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.common.ArithmeticOperator;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;


@AllArgsConstructor
public class BinaryArithmeticStatement implements IStatement {
    private IStatement lhs;
    private IStatement rhs;
    private ArithmeticOperator operator;

    @With
    @Getter
    private final FileLocation location;


    @Override
    public ExecutionResult<IStatement> execute(IVirtualMachine machine) throws ExecutionException {
        final ExecutionResult<IStatement> lhsExecuted = lhs.execute(machine);
        if (!lhsExecuted.isCompleted())
            return uncompleted(new BinaryArithmeticStatement(lhsExecuted.getValue(), rhs, operator, location));

        final ExecutionResult<IStatement> rhsExecuted = rhs.execute(machine);
        if (!rhsExecuted.isCompleted())
            return uncompleted(new BinaryArithmeticStatement(lhsExecuted.getValue(), rhsExecuted.getValue(),
                    operator, location));

        if (!(lhsExecuted.getValue() instanceof NumberValueStatement) ||
            !(rhsExecuted.getValue() instanceof NumberValueStatement))
            throw new ExecutionException("Expected numbers");

        final NumberValueStatement lhsNum = ((NumberValueStatement)lhsExecuted.getValue());
        final NumberValueStatement rhsNum = ((NumberValueStatement)rhsExecuted.getValue());
        final boolean castToInt = lhsNum.isInteger() && rhsNum.isInteger();

        final double result;
        switch (operator) {
            case ADD: {
                result = lhsNum.asDouble() + rhsNum.asDouble();
                break;
            }
            case SUB: {
                result = lhsNum.asDouble() - rhsNum.asDouble();
                break;
            }
            case MULTIPLY: {
                result = lhsNum.asDouble() * rhsNum.asDouble();
                break;
            }
            case DIVIDE: {
                result = castToInt ? lhsNum.asInt() / rhsNum.asInt()
                        : lhsNum.asDouble() / rhsNum.asDouble();
                break;
            }
            case POWER: {
                result = Math.pow(lhsNum.asDouble(), rhsNum.asDouble());
                break;
            }
            default:
                throw new ExecutionException("Unsupported binary operation");
        }
        return completed(castToInt
                ? new NumberValueStatement((long) result, location)
                : new NumberValueStatement(result, location));
    }
}
