package ru.nsu.logic.lang.execution.common;

public interface IPipeline {
    IContext getContext();
    IPipelineEntry getCurrentEntry();

    void pushEntry(final IPipelineEntry entry);
    void popEntry();
    boolean empty();
}
