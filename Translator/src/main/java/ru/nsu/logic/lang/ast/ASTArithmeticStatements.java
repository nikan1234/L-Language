package ru.nsu.logic.lang.ast;

import ru.nsu.logic.lang.ast.detail.ASTBinaryOperationStatement;
import ru.nsu.logic.lang.ast.detail.ASTUnaryOperationStatement;
import ru.nsu.logic.lang.common.ArithmeticOperator;

public class ASTArithmeticStatements {

    public static class UnaryOp extends ASTUnaryOperationStatement<ArithmeticOperator> {
        public UnaryOp(Node operand, ArithmeticOperator operator, FileLocation location) {
            super(operand, operator, location);
        }
    }

    public static class BinaryOp extends ASTBinaryOperationStatement<ArithmeticOperator> {
        public BinaryOp(Node lhs, Node rhs, ArithmeticOperator operator, FileLocation location) {
            super(lhs, rhs, operator, location);
        }
    }
}
