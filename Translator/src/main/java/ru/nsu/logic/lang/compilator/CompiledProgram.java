package ru.nsu.logic.lang.compilator;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import ru.nsu.logic.lang.base.IStatement;

import java.util.List;


@Builder
public class CompiledProgram {
    @Getter
    final private CompilationRegistry<CompiledClass> compiledClasses;

    @Getter
    final private CompilationRegistry<CompiledCallable> compiledFunctions;

    @Getter
    @Singular("statement")
    final private List<IStatement> body;
}
