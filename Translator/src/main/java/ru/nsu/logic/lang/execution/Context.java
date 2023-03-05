package ru.nsu.logic.lang.execution;

import ru.nsu.logic.lang.execution.common.IContext;
import ru.nsu.logic.lang.ast.FileLocation;

public class Context implements IContext {
    private final String functionName;
    private final FileLocation location;

    public Context(final String functionName, final FileLocation location) {
        this.functionName = functionName;
        this.location = location;
    }

    @Override
    public String getFunctionName() {
        return functionName;
    }

    @Override
    public FileLocation getLocation() {
        return location;
    }
}
