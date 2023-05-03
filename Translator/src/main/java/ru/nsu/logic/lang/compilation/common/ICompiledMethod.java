package ru.nsu.logic.lang.compilation.common;

import ru.nsu.logic.lang.common.AccessType;

import java.util.List;

public interface ICompiledMethod extends ICompilationRegistry.IEntry {
    ICompiledClass getOwner();

    List<String> getArguments();
    List<IStatement> getBody();
    AccessType getAccessType();
}
