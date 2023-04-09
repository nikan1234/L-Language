package ru.nsu.logic.lang.compilation.common;

import java.util.List;

public interface ICompiledClass extends ICompilationRegistry.IEntry {
    String CTOR_NAME = "__constructor__";

    List<IMember> getMembers();
    ICompiledMethod getConstructor();
    ICompiledMethod getMethod(final String name);
}
