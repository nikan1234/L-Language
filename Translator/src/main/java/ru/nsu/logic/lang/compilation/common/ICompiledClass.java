package ru.nsu.logic.lang.compilation.common;

public interface ICompiledClass extends ICompilationRegistry.IEntry {
    ICompiledFunction getMethod(final String name);
}
