package ru.nsu.logic.lang.excution.common;

import ru.nsu.logic.lang.grammar.common.IStatement;

public interface IPipelineEntry {
    String getName();

    void initializeVariable(final String varName, final IStatement value);
    IStatement getInitializedVariable(final String varName) throws ExecutionException;

    String pushTempVariable();
    String popTempVariable();

    boolean completed();

    void nextStatement();
    IStatement getCurrentStatement();
    void setCurrentStatement(final IStatement statement);
}
