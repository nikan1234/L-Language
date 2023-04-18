package ru.nsu.logic.lang.compilation.common;

import java.util.List;
import java.util.Optional;

public interface ICompiledClass extends ICompilationRegistry.IEntry {
    String CTOR_NAME = "__constructor__";

    List<IMember> getMembers();
    Optional<ICompiledMethod> getConstructor();
    Optional<ICompiledMethod> getMethod(final String name);
}
