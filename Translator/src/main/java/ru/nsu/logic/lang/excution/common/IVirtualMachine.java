package ru.nsu.logic.lang.base.execution;

import ru.nsu.logic.lang.base.compilation.ICompilationRegistry;
import ru.nsu.logic.lang.base.compilation.ICompiledClass;
import ru.nsu.logic.lang.base.compilation.ICompiledFunction;
import ru.nsu.logic.lang.base.grammar.IStatement;

public interface IVirtualMachine {
    ICompilationRegistry<ICompiledFunction> getCompiledFunctions();
    ICompilationRegistry<ICompiledClass> getCompiledClasses();

    IScreen getScreen();
    IPipeline getPipeline();

    void run() throws ExecutionException;
    IStatement onPushEntry(final IPipelineEntry entry);
    void onEntryCompleted(final IStatement result);
}
