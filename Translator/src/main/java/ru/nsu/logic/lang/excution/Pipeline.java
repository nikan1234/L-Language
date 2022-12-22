package ru.nsu.logic.lang.excution;

import ru.nsu.logic.lang.base.execution.IPipeline;
import ru.nsu.logic.lang.base.execution.IPipelineEntry;

import java.util.Stack;

public class Pipeline implements IPipeline {
    private final Stack<IPipelineEntry> pipeline = new Stack<>();

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
