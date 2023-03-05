package ru.nsu.logic.lang.compilation.common;

import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.common.IExecutable;

public interface IStatement extends IExecutable<IStatement> {
    /**
     Returns location of statement in source file
     */
    FileLocation getLocation();

    /**
     Returns copy of statement with different location
     */
    IStatement withLocation(FileLocation location);
}
