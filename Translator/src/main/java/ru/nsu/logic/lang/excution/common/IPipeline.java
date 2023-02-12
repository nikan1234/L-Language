package ru.nsu.logic.lang.excution.common;

public interface IPipeline {
    IPipelineEntry getCurrentEntry();
    void pushEntry(final IPipelineEntry entry);
    void popEntry();
    boolean empty();
}
