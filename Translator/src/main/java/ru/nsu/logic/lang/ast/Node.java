package ru.nsu.logic.lang.ast;

import java.util.List;

public interface Node {

    /** This method is called after the node has been made the current
     node.  It indicates that child nodes can now be added to it. */
    void jjtOpen();

    /** This method is called after all the child nodes have been
     added. */
    void jjtClose();

    /** This pair of methods are used to inform the node of its
     parent. */
    void jjtSetParent(Node n);
    Node jjtGetParent();

    /** This method tells the node to add its argument to the node's
     list of children.  */
    void jjtAddChild(Node n, int i);

    /** This method returns a child node.  The children are numbered
     from zero, left to right. */
    Node jjtGetChild(int i);

    /** Return the number of children the node has. */
    int jjtGetNumChildren();

    /** Return all children */
    List<Node> jjtGetChildren();

    /** This pair of methods are used to operate with location of node
     in source file. */
    void jjtSetLocation(FileLocation location);
    FileLocation jjtGetLocation();

    /** Functions to operate with value stored in node. */
    void jjtSetValue(Object value);
    Object jjtGetValue();
    <T> T jjtGetValueAs(final Class<T> as);
}
