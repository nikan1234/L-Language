package ru.nsu.logic.lang.ast.detail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.ast.Node;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class ASTUnaryOperationStatement<OperatorType> implements Node {
    private Node operand;
    @Getter
    private OperatorType operator;
    private FileLocation location;

    @Override
    public void jjtOpen() {}

    @Override
    public void jjtClose() {}

    @Override
    public void jjtSetParent(Node n) { throw new UnsupportedOperationException(); }

    @Override
    public Node jjtGetParent() { return null; }

    @Override
    public void jjtAddChild(Node n, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int jjtGetNumChildren() {
        return 1;
    }

    @Override
    public Node jjtGetChild(int i) {
        return operand;
    }

    @Override
    public List<Node> jjtGetChildren() {
        return Collections.singletonList(operand);
    }

    @Override
    public void jjtSetLocation(FileLocation location) { this.location = location; }

    @Override
    public FileLocation jjtGetLocation() {
        return this.location;
    }

    @Override
    public void jjtSetValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object jjtGetValue() {
        return operator;
    }

    @Override
    public <T> T jjtGetValueAs(Class<T> as) {
        return as.cast(operator);
    }
}
