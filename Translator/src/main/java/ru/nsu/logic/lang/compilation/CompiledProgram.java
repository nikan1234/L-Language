package ru.nsu.logic.lang.compilation;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import ru.nsu.logic.lang.compilation.common.ICompiledClass;
import ru.nsu.logic.lang.compilation.common.ICompiledFunction;
import ru.nsu.logic.lang.grammar.common.IStatement;

import java.util.List;


@Builder
public class CompiledProgram {
    @Getter
    final private CompilationRegistry<ICompiledClass> compiledClasses;

    @Getter
    final private CompilationRegistry<ICompiledFunction> compiledFunctions;

    @Getter
    @Singular("statement")
    final private List<IStatement> body;
}
