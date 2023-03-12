package ru.nsu.logic.lang.compilation.statements.logic;

import lombok.AllArgsConstructor;
import lombok.Setter;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.common.ComparisonOperator;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.compilation.statements.NumberValue;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@AllArgsConstructor
public class ComparisonFormula implements IFormula {

  @Setter
  private IStatement left;
  @Setter
  private IStatement right;
  @Setter
  private ComparisonOperator operator;
  @Setter
  private FileLocation location;

  private static final Map<ComparisonOperator, BiFunction<Double, Double, Boolean>> OP_TO_FUNCTION;
  static {
    OP_TO_FUNCTION =  new HashMap<>();
    OP_TO_FUNCTION.put(ComparisonOperator.EQ, Double::equals);
    OP_TO_FUNCTION.put(ComparisonOperator.NE, (l, r) -> !l.equals(r));
    OP_TO_FUNCTION.put(ComparisonOperator.LT, (l, r) -> l < r);
    OP_TO_FUNCTION.put(ComparisonOperator.LE, (l, r) -> l <= r);
    OP_TO_FUNCTION.put(ComparisonOperator.GT, (l, r) -> l > r);
    OP_TO_FUNCTION.put(ComparisonOperator.GE, (l, r) -> l >= r);
  }

  @Override
  public ExecutionResult<IFormula> execute(final IVirtualMachine machine) throws ExecutionException {
    final ExecutionResult<IStatement> leftExecuted = left.execute(machine);
    if (!leftExecuted.isCompleted())
      return new ExecutionResult<>(
              new ComparisonFormula(leftExecuted.getValue(), right, operator, location), false);

    final ExecutionResult<IStatement> rightExecuted = right.execute(machine);
    if (!rightExecuted.isCompleted())
      return new ExecutionResult<>(
              new ComparisonFormula(left, rightExecuted.getValue(), operator, location), false);

    /// Evaluate logical expression
    final boolean cmpResult = OP_TO_FUNCTION.get(operator)
            .apply(asDouble(leftExecuted.getValue()), asDouble(rightExecuted.getValue()));
    return new ExecutionResult<>(new BooleanValue(cmpResult), true);
  }

  private double asDouble(final IStatement statement) throws ExecutionException {
    if (!(statement instanceof NumberValue))
      throw new ExecutionException("Expected number");
    return ((NumberValue) statement).asDouble();
  }
}