package ru.nsu.logic.lang.excution;

import ru.nsu.logic.lang.excution.common.IContext;
import ru.nsu.logic.lang.excution.common.IPipeline;
import ru.nsu.logic.lang.excution.common.IPipelineEntry;

import java.util.Stack;

public class Pipeline implements IPipeline {
    private final Stack<IPipelineEntry> pipeline = new Stack<>();

    @Override
    public IContext getContext() {
        final IPipelineEntry entry = getCurrentEntry();
        return new Context(
                entry.getName(),
                entry.getCurrentStatement().getLocation());
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
