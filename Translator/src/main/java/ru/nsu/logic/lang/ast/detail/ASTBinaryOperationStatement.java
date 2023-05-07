package ru.nsu.logic.lang.ast.detail;


import lombok.AllArgsConstructor;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.ast.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class ASTBinaryOperationStatement<OperatorType> implements Node {
    private final Node lhs;
    private final Node rhs;
    private final OperatorType operator;
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
        return 2;
    }

    @Override
    public Node jjtGetChild(int i) {
        return jjtGetChildren().get(i);
    }

    @Override
    public List<Node> jjtGetChildren() {
        final Node [] children = { lhs, rhs };
        return new ArrayList<>(Arrays.asList(children));
    }

    @Override
    public void jjtSetLocation(FileLocation location) {
        this.location = location;
    }

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
