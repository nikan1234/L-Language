package ru.nsu.logic.lang.base.execution;

import ru.nsu.logic.lang.base.compilation.ICompilationRegistry;
import ru.nsu.logic.lang.base.compilation.ICompiledClass;
import ru.nsu.logic.lang.base.compilation.ICompiledFunction;

public interface IVirtualMachine {
    ICompilationRegistry<ICompiledFunction> getCompiledFunctions();
    ICompilationRegistry<ICompiledClass> getCompiledClasses();

    IScreen getScreen();
    IPipeline getPipeline();

    void run() throws ExecutionException;
}
