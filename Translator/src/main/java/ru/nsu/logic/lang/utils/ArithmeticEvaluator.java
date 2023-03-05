package ru.nsu.logic.lang.utils;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.compilation.statements.NumberValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArithmeticEvaluator {

    private final Expression expression;
    private final FileLocation location;

    public ArithmeticEvaluator(final List<IStatement> operands,
                               final List<String> operators,
                               final FileLocation location) {
        assertNumberTypes(operands);

        final StringBuilder expression = new StringBuilder(generateOperandName(0));
        final Map<String, Double> varsToVals = new HashMap<>();
        varsToVals.put(generateOperandName(0), ((NumberValue)operands.get(0)).asDouble());

        for (int i = 0; i < operators.size(); ++i) {
            final String varName = generateOperandName(i + 1);
            expression.append(operators.get(i));
            expression.append(varName);
            varsToVals.put(varName, ((NumberValue)operands.get(i + 1)).asDouble());
        }

        this.expression = new ExpressionBuilder(expression.toString())
                .variables(varsToVals.keySet())
                .build()
                .setVariables(varsToVals);
        if (!this.expression.validate().isValid())
            throw new RuntimeException("Invalid expression");

        this.location = location;
    }

    public IStatement calculate() {
        final double evaluated =  expression.evaluate();

        if ((evaluated == Math.floor(evaluated)) && !Double.isInfinite(evaluated))
            return new NumberValue((int) evaluated, location);
        return new NumberValue(evaluated, location);
    }

    private static String generateOperandName(final int operandNumber) {
        return "_var" + operandNumber;
    }

    private static void assertNumberTypes(List<IStatement> args){
        if (!args.stream().allMatch(a -> (a instanceof NumberValue)))
            throw new IllegalArgumentException("Expected numeric values");
    }
}
