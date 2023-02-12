package ru.nsu.logic.lang.utils;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import ru.nsu.logic.lang.grammar.common.IStatement;
import ru.nsu.logic.lang.grammar.NumberValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArithmeticCalculator {

    private final Expression expression;

    public ArithmeticCalculator(final List<IStatement> operands,
                                final List<String> operators) {
        assertNumberTypes(operands);

        final StringBuilder expression = new StringBuilder(generateOperandName(0));
        final Map<String, Double> varsToVals = new HashMap<>();
        varsToVals.put(generateOperandName(0), ((NumberValue)operands.get(0)).getNumber().doubleValue());

        for (int i = 0; i < operators.size(); ++i) {
            final String varName = generateOperandName(i + 1);
            expression.append(operators.get(i));
            expression.append(varName);
            varsToVals.put(varName, ((NumberValue)operands.get(i + 1)).getNumber().doubleValue());
        }

        this.expression = new ExpressionBuilder(expression.toString())
                .variables(varsToVals.keySet())
                .build()
                .setVariables(varsToVals);
        if (!this.expression.validate().isValid())
            throw new RuntimeException("Invalid expression");
    }

    public IStatement calculate() {
        final NumberValue number = new NumberValue(0);
        final double evaluated =  expression.evaluate();

        if ((evaluated == Math.floor(evaluated)) && !Double.isInfinite(evaluated))
            number.setNumber((int) evaluated);
        else
            number.setNumber(evaluated);

        return number;
    }

    private static String generateOperandName(final int operandNumber) {
        return "_var" + operandNumber;
    }

    private static void assertNumberTypes(List<IStatement> args){
        if (!args.stream().allMatch(a -> (a instanceof NumberValue)))
            throw new IllegalArgumentException("Expected numeric values");
    }
}
