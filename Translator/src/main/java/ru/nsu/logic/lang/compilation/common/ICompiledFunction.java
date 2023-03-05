package ru.nsu.logic.lang.compilation.common;

import java.util.List;

public interface ICompiledFunction extends ICompilationRegistry.IEntry {
    List<String> getArguments();
    List<IStatement> getBody();
}
