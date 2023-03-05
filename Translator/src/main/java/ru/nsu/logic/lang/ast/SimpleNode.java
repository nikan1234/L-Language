package ru.nsu.logic.lang.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SimpleNode implements Node {

    protected Node parent;
    protected Node[] children;
    protected int id;
    protected LStatement parser;
    protected Object value;
    protected FileLocation location;

    public SimpleNode(final int id) {
        this.id = id;
    }

    public SimpleNode(final LStatement p, final int id) {
        this.parser = p;
        this.id = id;
    }

    @Override
    public void jjtOpen() {
    }

    @Override
    public void jjtClose() {
    }

    @Override
    public void jjtSetParent(final Node n) { parent = n; }

    @Override
    public Node jjtGetParent() { return parent; }

    @Override
    public void jjtAddChild(final Node n, final int i) {
        if (children == null) {
            children = new Node[i + 1];
        } else if (i >= children.length) {
            Node c[] = new Node[i + 1];
            System.arraycopy(children, 0, c, 0, children.length);
            children = c;
        }
        children[i] = n;
    }

    @Override
    public Node jjtGetChild(int i) {
        return children[i];
    }

    @Override
    public int jjtGetNumChildren() {
        return (children == null) ? 0 : children.length;
    }

    @Override
    public List<Node> jjtGetChildren() {
        return (children == null) ? new ArrayList<>() : Arrays.asList(children);
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
        this.value = value;
    }

    @Override
    public Object jjtGetValue() {
        return value;
    }

    @Override
    public <T> T jjtGetValueAs(final Class<T> as) { return as.cast(jjtGetValue()); }

    @Override
    public String toString() {
        return LStatementTreeConstants.jjtNodeName[id];
    }
}