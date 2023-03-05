package ru.nsu.logic.lang.execution.common;

import ru.nsu.logic.lang.compilation.common.IStatement;

public interface IVirtualMachine {
    void run() throws ExecutionException;

    IScreen getScreen();
    IPipeline getPipeline();

    IStatement onPipelineExtend(final IStatement statement) throws ExecutionException;
    void onPipelineRollback(final IStatement statement) throws ExecutionException;
}
