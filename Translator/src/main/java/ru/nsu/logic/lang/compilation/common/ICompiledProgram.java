package ru.nsu.logic.lang.compilation.common;

import java.util.List;

public interface ICompiledProgram {
    ICompilationRegistry<ICompiledFunction> getCompiledFunctions();
    ICompilationRegistry<ICompiledClass> getCompiledClasses();
    List<IStatement> getStatements();
}
