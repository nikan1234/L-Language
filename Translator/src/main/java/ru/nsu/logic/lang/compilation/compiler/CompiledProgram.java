package ru.nsu.logic.lang.compilation.compiler;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import ru.nsu.logic.lang.compilation.common.*;

import java.util.List;


@Builder
public class CompiledProgram implements ICompiledProgram {
    @Getter
    final private ICompilationRegistry<ICompiledClass> compiledClasses;

    @Getter
    final private ICompilationRegistry<ICompiledFunction> compiledFunctions;

    @Getter
    @Singular("statement")
    final private List<IStatement> statements;
}
