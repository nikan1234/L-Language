package ru.nsu.logic.lang.base.execution;

import ru.nsu.logic.lang.base.grammar.IStatement;

import java.util.function.Function;

public interface IPipelineEntry {
    void initializeVariable(final String varName, final IStatement value);
    IStatement getInitializedVariable(final String varName) throws ExecutionException;

    String addUniqueTemporaryVariable();

    void nextStatement();
    boolean completed();
    IStatement getCurrentStatement();
    void setCurrentStatement(final IStatement statement);
}
