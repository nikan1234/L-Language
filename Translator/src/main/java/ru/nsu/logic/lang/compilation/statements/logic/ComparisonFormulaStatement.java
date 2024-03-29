package ru.nsu.logic.lang.compilation.statements.logic;

import lombok.AllArgsConstructor;
import lombok.Setter;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.common.ComparisonOperator;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.compilation.statements.NumberValueStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@AllArgsConstructor
public class ComparisonFormulaStatement implements IFormula {

  @Setter
  private IStatement left;
  @Setter
  private IStatement right;
  @Setter
  private ComparisonOperator operator;
  @Setter
  private FileLocation location;

  private static final Map<ComparisonOperator, BiFunction<Double, Double, Boolean>> NUMERIC_OP_TO_FUNCTION;
  private static final Map<ComparisonOperator, BiFunction<Object, Object, Boolean>> OBJECT_OP_TO_FUNCTION;
  static {
    NUMERIC_OP_TO_FUNCTION =  new HashMap<>();
    NUMERIC_OP_TO_FUNCTION.put(ComparisonOperator.EQ, Double::equals);
    NUMERIC_OP_TO_FUNCTION.put(ComparisonOperator.NE, (l, r) -> !l.equals(r));
    NUMERIC_OP_TO_FUNCTION.put(ComparisonOperator.LT, (l, r) -> l < r);
    NUMERIC_OP_TO_FUNCTION.put(ComparisonOperator.LE, (l, r) -> l <= r);
    NUMERIC_OP_TO_FUNCTION.put(ComparisonOperator.GT, (l, r) -> l > r);
    NUMERIC_OP_TO_FUNCTION.put(ComparisonOperator.GE, (l, r) -> l >= r);

    OBJECT_OP_TO_FUNCTION = new HashMap<>();
    OBJECT_OP_TO_FUNCTION.put(ComparisonOperator.EQ, Object::equals);
    OBJECT_OP_TO_FUNCTION.put(ComparisonOperator.NE, (l, r) -> !l.equals(r));
  }

  @Override
  public ExecutionResult<IFormula> execute(final IVirtualMachine machine) throws ExecutionException {
    final ExecutionResult<IStatement> leftExecuted = left.execute(machine);
    if (!leftExecuted.isCompleted())
      return uncompleted(new ComparisonFormulaStatement(leftExecuted.getValue(), right, operator, location));

    final ExecutionResult<IStatement> rightExecuted = right.execute(machine);
    if (!rightExecuted.isCompleted())
      return uncompleted(new ComparisonFormulaStatement(left, rightExecuted.getValue(), operator, location));

    /// If one of operands is null or list then only = and != operators are supported
    if (!(leftExecuted.getValue() instanceof NumberValueStatement) ||
        !(rightExecuted.getValue() instanceof NumberValueStatement)) {
      final BiFunction<Object, Object, Boolean> op = OBJECT_OP_TO_FUNCTION.getOrDefault(operator, null);
      if (op == null)
        throw new ExecutionException("Unsupported operation");

      return completed(new BooleanValueStatement(op.apply(leftExecuted.getValue(), rightExecuted.getValue())));
    }


    /// Evaluate logical expression for numbers
    final boolean cmpResult = NUMERIC_OP_TO_FUNCTION.get(operator)
            .apply(asDouble(leftExecuted.getValue()), asDouble(rightExecuted.getValue()));
    return completed(new BooleanValueStatement(cmpResult));
  }

  private double asDouble(final IStatement statement) throws ExecutionException {
    if (!(statement instanceof NumberValueStatement))
      throw new ExecutionException("Expected number");
    return ((NumberValueStatement) statement).asDouble();
  }
}