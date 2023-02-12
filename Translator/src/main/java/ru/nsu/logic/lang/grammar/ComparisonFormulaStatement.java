package ru.nsu.logic.lang.grammar;

import lombok.Setter;
import ru.nsu.logic.lang.excution.common.ExecutionException;
import ru.nsu.logic.lang.excution.common.IVirtualMachine;
import ru.nsu.logic.lang.grammar.common.ComparisonOperator;
import ru.nsu.logic.lang.grammar.common.IStatement;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class ComparisonFormulaStatement extends SimpleNode implements IStatement {

  static Map<ComparisonOperator, BiFunction<Double, Double, BooleanValue>> OP_TO_FUNCTION;
  static {
    OP_TO_FUNCTION =  new HashMap<>();
    OP_TO_FUNCTION.put(ComparisonOperator.EQ, (l, r) -> new BooleanValue(l.equals(r)));
    OP_TO_FUNCTION.put(ComparisonOperator.NE, (l, r) -> new BooleanValue(!l.equals(r)));
    OP_TO_FUNCTION.put(ComparisonOperator.LT, (l, r) -> new BooleanValue(l < r));
    OP_TO_FUNCTION.put(ComparisonOperator.LE, (l, r) -> new BooleanValue(l <= r));
    OP_TO_FUNCTION.put(ComparisonOperator.GT, (l, r) -> new BooleanValue(l > r));
    OP_TO_FUNCTION.put(ComparisonOperator.GE, (l, r) -> new BooleanValue(l >= r));
  }

  @Setter
  private IStatement left;
  @Setter
  private IStatement right;
  @Setter
  private ComparisonOperator operator;

  public ComparisonFormulaStatement(int id) {
    super(id);
  }

  private ComparisonFormulaStatement(final IStatement left, final IStatement right) {
    this.left = left;
    this.right = right;
  }

  @Override
  public ExecutionResult execute(final IVirtualMachine machine) throws ExecutionException {
    if (!left.executedInPlace())
      return new ExecutionResult(
              new ComparisonFormulaStatement(left.execute(machine).getStatement(), right),
              false);

    if (!right.executedInPlace())
      return new ExecutionResult(
              new ComparisonFormulaStatement(left, right.execute(machine).getStatement()),
              false);

    final BooleanValue cmpResult = OP_TO_FUNCTION.get(operator).apply(asDouble(left), asDouble(right));
    return new ExecutionResult(cmpResult, true);
  }

  @Override
  public boolean executedInPlace() {
    return left.executedInPlace() && right.executedInPlace();
  }

  private double asDouble(final IStatement statement) throws ExecutionException {
    if (!(statement instanceof NumberValue))
      throw new ExecutionException("Expected number");
    return ((NumberValue) statement).asDouble();
  }
}