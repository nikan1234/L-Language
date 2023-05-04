package ru.nsu.logic.lang.ast;

import ru.nsu.logic.lang.common.LogicOperator;

public class ASTLogicStatements {

    public static class Unary extends ASTUnaryStatement<LogicOperator> {
        public Unary(Node operand, LogicOperator operator, FileLocation location) {
            super(operand, operator, location);
        }
    }

    public static class Binary extends ASTBinaryStatement<LogicOperator> {
        public Binary(Node lhs, Node rhs, LogicOperator operator, FileLocation location) {
            super(lhs, rhs, operator, location);
        }
    }
}
