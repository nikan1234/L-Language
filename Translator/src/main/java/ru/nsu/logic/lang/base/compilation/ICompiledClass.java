package ru.nsu.logic.lang.base.compilation;

public interface ICompiledClass extends ICompilationRegistry.IEntry {
    ICompiledFunction getMethod(final String name);
}
