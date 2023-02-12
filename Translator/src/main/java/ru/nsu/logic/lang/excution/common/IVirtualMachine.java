package ru.nsu.logic.lang.excution.common;

import ru.nsu.logic.lang.compilation.common.ICompilationRegistry;
import ru.nsu.logic.lang.compilation.common.ICompiledClass;
import ru.nsu.logic.lang.compilation.common.ICompiledFunction;
import ru.nsu.logic.lang.grammar.common.IStatement;

public interface IVirtualMachine {
    ICompilationRegistry<ICompiledFunction> getCompiledFunctions();
    ICompilationRegistry<ICompiledClass> getCompiledClasses();

    IScreen getScreen();
    IPipeline getPipeline();

    void run() throws ExecutionException;
    IStatement onPushEntry(final IPipelineEntry entry);
    void onEntryCompleted(final IStatement result);
}
