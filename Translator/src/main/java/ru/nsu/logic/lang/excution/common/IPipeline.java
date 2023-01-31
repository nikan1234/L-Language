package ru.nsu.logic.lang.base.execution;

public interface IPipeline {
    IPipelineEntry getCurrentEntry();
    void pushEntry(final IPipelineEntry entry);
    void popEntry();
    boolean empty();
}
