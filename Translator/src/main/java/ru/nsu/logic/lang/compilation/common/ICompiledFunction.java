package ru.nsu.logic.lang.base.compilation;

import ru.nsu.logic.lang.base.grammar.IStatement;

import java.util.List;

public interface ICompiledFunction extends ICompilationRegistry.IEntry {
    List<String> getArguments();
    List<IStatement> getBody();
}
