package ru.nsu.logic.lang.ast.detail;

import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.ast.LStatementTreeConstants;
import ru.nsu.logic.lang.ast.Node;

import java.util.Collections;
import java.util.List;

public class SingleChildNode implements Node {

    private final int id;
    private Node parent = null;
    private Node child = null;
    private FileLocation location = null;

    public SingleChildNode(final int id) {
        this.id = id;
    }

    public void jjtAssignChild(final Node child) {
        this.child = child;
    }
    public Node jjtGetChild() { return this.child; }

    @Override
    public void jjtOpen() {}

    @Override
    public void jjtClose() {}

    @Override
    public void jjtSetParent(final Node n) { this.parent = n; }

    @Override
    public Node jjtGetParent() { return parent; }

    @Override
    public void jjtAddChild(Node n, int i) {}

    @Override
    public Node jjtGetChild(int i) { return jjtGetChild(); }

    @Override
    public int jjtGetNumChildren() { return child == null ? 1 : 0; }

    @Override
    public List<Node> jjtGetChildren() {
        return child == null ? Collections.emptyList() : Collections.singletonList(child);
    }

    @Override
    public void jjtSetLocation(final FileLocation location) {
        this.location = location;
    }

    @Override
    public FileLocation jjtGetLocation() {
        return location;
    }

    @Override
    public void jjtSetValue(final Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object jjtGetValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T jjtGetValueAs(final Class<T> as) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return LStatementTreeConstants.jjtNodeName[id];
    }
}
