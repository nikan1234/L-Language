package ru.nsu.logic.lang.grammar;

public class SimpleNode implements Node {

    protected Node parent;
    protected Node[] children;
    protected int id;
    protected Object value;
    protected LStatement parser;

    protected static final int GENERATED_STATEMENT_ID = -1;

    public SimpleNode() {
        this(GENERATED_STATEMENT_ID);
    }

    public SimpleNode(int id) {
        this.id = id;
    }

    public SimpleNode(LStatement p, int id) {
        this(id);
        parser = p;
    }

    public void jjtOpen() {
    }

    public void jjtClose() {
    }

    public void jjtSetParent(Node n) { parent = n; }
    public Node jjtGetParent() { return parent; }

    public void jjtAddChild(Node n, int i) {
        if (children == null) {
            children = new Node[i + 1];
        } else if (i >= children.length) {
            Node c[] = new Node[i + 1];
            System.arraycopy(children, 0, c, 0, children.length);
            children = c;
        }
        children[i] = n;
    }

    public Node jjtGetChild(int i) {
        return children[i];
    }

    public int jjtGetNumChildren() {
        return (children == null) ? 0 : children.length;
    }

    public void jjtSetValue(Object value) { this.value = value; }
    public Object jjtGetValue() { return value; }

  /* You can override these two methods in subclasses of SimpleNode to
     customize the way the node appears when the tree is dumped.  If
     your output uses more than one line you should override
     toString(String), otherwise overriding toString() is probably all
     you need to do. */

    public String toString() {
        if (id >= 0)
            return LStatementTreeConstants.jjtNodeName[id];
        return "unknown";
    }
}