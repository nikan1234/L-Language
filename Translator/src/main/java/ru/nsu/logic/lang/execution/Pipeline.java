package ru.nsu.logic.lang.execution;

import ru.nsu.logic.lang.execution.common.IContext;
import ru.nsu.logic.lang.execution.common.IPipeline;
import ru.nsu.logic.lang.execution.common.IPipelineEntry;

import java.util.Stack;

public class Pipeline implements IPipeline {
    private final Stack<IPipelineEntry> pipeline = new Stack<>();

    @Override
    public IContext getCurrentContext() {
        final IPipelineEntry entry = getCurrentEntry();
        return entry.getContext();
    }

    @Override
    public IPipelineEntry getCurrentEntry() {
        return pipeline.peek();
    }

    @Override
    public void pushEntry(IPipelineEntry entry) {
        pipeline.push(entry);
    }

    @Override
    public void popEntry() {
        pipeline.pop();
    }

    @Override
    public boolean empty() {
        return pipeline.empty();
    }
}
