package ru.nsu.logic.lang.execution.common;

import ru.nsu.logic.lang.compilation.common.IStatement;

public interface IPipelineEntry {
    String getName();

    void initializeVariable(final String varName, final IStatement value);
    IStatement getInitializedVariable(final String varName) throws ExecutionException;

    boolean hasTempVariable();
    String pushTempVariable();
    String popTempVariable();

    boolean completed();

    void nextStatement();
    IStatement getCurrentStatement();
    void setCurrentStatement(final IStatement statement);
}
