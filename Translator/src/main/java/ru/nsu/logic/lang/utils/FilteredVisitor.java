package ru.nsu.logic.lang.utils;

import ru.nsu.logic.lang.grammar.Node;

import java.util.LinkedList;
import java.util.List;

public class FilteredVisitor<T> {
    private final Class<T> targetType;

    private List<T> traverse(final Node node) {
        final List<T> result = new LinkedList<>();

        if (targetType.isInstance(node))
            result.add(targetType.cast(node));
        else
            for (int i = 0; i < node.jjtGetNumChildren(); ++i)
                result.addAll(traverse(node.jjtGetChild(i)));
        return result;
    }

    public FilteredVisitor(Class<T> targetType) {
        this.targetType = targetType;
    }

    public List<T> children(final Node root) {
        return traverse(root);
    }
}
