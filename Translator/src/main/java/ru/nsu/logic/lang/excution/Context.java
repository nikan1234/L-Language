package ru.nsu.logic.lang.excution;

import ru.nsu.logic.lang.excution.common.IContext;
import ru.nsu.logic.lang.grammar.common.FileLocation;

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
