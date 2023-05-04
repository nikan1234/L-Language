package ru.nsu.logic.lang.ast;

import ru.nsu.logic.lang.common.ArithmeticOperator;

public class ASTArithmeticStatements {

    public static class Unary extends ASTUnaryStatement<ArithmeticOperator> {
        public Unary(Node operand, ArithmeticOperator operator, FileLocation location) {
            super(operand, operator, location);
        }
    }

    public static class Binary extends ASTBinaryStatement<ArithmeticOperator> {
        public Binary(Node lhs, Node rhs, ArithmeticOperator operator, FileLocation location) {
            super(lhs, rhs, operator, location);
        }
    }
}
