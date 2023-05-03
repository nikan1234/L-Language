package ru.nsu.logic.lang.compilation.common;

import ru.nsu.logic.lang.common.AccessType;
import ru.nsu.logic.lang.execution.common.ExecutionException;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public interface ICompiledClass extends ICompilationRegistry.IEntry {
    String CTOR_NAME = "__constructor__";

    Optional<ICompiledClass> getBase();

    List<IMember> enumerateMembers();

    IMember accessMember(final String name, final EnumSet<AccessType> access) throws ExecutionException;
    ICompiledMethod accessMethod(final String name, final EnumSet<AccessType> access) throws ExecutionException;
    Optional<ICompiledMethod> getConstructor(final EnumSet<AccessType> access) throws ExecutionException;
}
