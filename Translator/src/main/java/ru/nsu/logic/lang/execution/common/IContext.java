package ru.nsu.logic.lang.execution.common;

import ru.nsu.logic.lang.ast.FileLocation;

public interface IContext {
    String getFunctionName();
    FileLocation getLocation();
}
