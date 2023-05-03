package ru.nsu.logic.lang.compilation.common;

import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.common.AccessType;

public interface IMember {
    ICompiledClass getOwner();

    String getName();
    AccessType getAccessType();
    FileLocation getLocation();
}
