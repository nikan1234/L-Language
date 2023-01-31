package ru.nsu.logic.lang.base.execution;

import ru.nsu.logic.lang.base.grammar.IStatement;

public interface IPipelineEntry {
    void initializeVariable(final String varName, final IStatement value);
    IStatement getInitializedVariable(final String varName) throws ExecutionException;

    String pushTempVariable();
    String popTempVariable();

    boolean completed();

    void nextStatement();
    IStatement getCurrentStatement();
    void setCurrentStatement(final IStatement statement);
}
