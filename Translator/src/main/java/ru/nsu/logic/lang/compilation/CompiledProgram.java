package ru.nsu.logic.lang.compilator;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import ru.nsu.logic.lang.base.compilation.ICompiledClass;
import ru.nsu.logic.lang.base.compilation.ICompiledFunction;
import ru.nsu.logic.lang.base.grammar.IStatement;

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
