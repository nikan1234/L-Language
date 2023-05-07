package ru.nsu.logic.lang.ast;

import ru.nsu.logic.lang.ast.detail.ASTBinaryOperationStatement;
import ru.nsu.logic.lang.ast.detail.ASTUnaryOperationStatement;
import ru.nsu.logic.lang.common.LogicOperator;

public class ASTLogicStatements {

    public static class UnaryOp extends ASTUnaryOperationStatement<LogicOperator> {
        public UnaryOp(Node operand, LogicOperator operator, FileLocation location) {
            super(operand, operator, location);
        }
    }

    public static class BinaryOp extends ASTBinaryOperationStatement<LogicOperator> {
        public BinaryOp(Node lhs, Node rhs, LogicOperator operator, FileLocation location) {
            super(lhs, rhs, operator, location);
        }
    }
}
