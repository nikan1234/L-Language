package ru.nsu.logic.lang.grammar;

import lombok.Setter;
import ru.nsu.logic.lang.excution.common.ExecutionException;
import ru.nsu.logic.lang.excution.common.IVirtualMachine;
import ru.nsu.logic.lang.grammar.common.ComparisonOperator;
import ru.nsu.logic.lang.grammar.common.IFormula;
import ru.nsu.logic.lang.grammar.common.IStatement;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class ComparisonFormula extends SimpleNode implements IFormula {

  @Setter
  private IStatement left;
  @Setter
  private IStatement right;
  @Setter
  private ComparisonOperator operator;

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

  public ComparisonFormula(int id) {
    super(id);
  }

  private ComparisonFormula(final IStatement left, final IStatement right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public ExecutionResult<IFormula> execute(final IVirtualMachine machine) throws ExecutionException {
    final ExecutionResult<IStatement> leftExecuted = left.execute(machine);
    if (!leftExecuted.isCompleted())
      return new ExecutionResult<>(new ComparisonFormula(leftExecuted.getValue(), right), false);

    final ExecutionResult<IStatement> rightExecuted = right.execute(machine);
    if (!rightExecuted.isCompleted())
      return new ExecutionResult<>(new ComparisonFormula(left, rightExecuted.getValue()), false);

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