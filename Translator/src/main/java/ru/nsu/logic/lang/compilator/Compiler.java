package ru.nsu.logic.lang.compilator;

import ru.nsu.logic.lang.base.IStatement;
import ru.nsu.logic.lang.grammar.*;
import ru.nsu.logic.lang.utils.FilteredVisitor;

import java.util.List;

public class Compiler {

    public CompiledProgram compile(final LLangProgram program) {
        final CompilationRegistry<CompiledClass> compiledClasses = new CompilationRegistry<>();
        final CompilationRegistry<CompiledCallable> compiledFunctions = new CompilationRegistry<>();;

        final CompiledProgram.CompiledProgramBuilder builder = CompiledProgram.builder();
        for (int i = 0; i < program.jjtGetNumChildren(); ++i) {
            final Node node = program.jjtGetChild(i);

            if (node instanceof IStatement)
                builder.statement((IStatement) node);

            if (node instanceof FunctionDeclaration)
                compiledFunctions.add(compile((FunctionDeclaration) node));

            if (node instanceof ClassDeclaration)
                compiledClasses.add(compile((ClassDeclaration) node));

        }
        return builder
                .compiledClasses(compiledClasses)
                .compiledFunctions(compiledFunctions)
                .build();
    }

    private CompiledCallable compile(final FunctionDeclaration decl) {
        final CompiledCallable.CompiledCallableBuilder builder = CompiledCallable.builder();
        builder.name(decl.getName());
        builder.accessType(AccessTypeEnum.PUBLIC);

        new FilteredVisitor<>(ArgumentDeclaration.class).children(decl)
                .forEach(arg -> builder.arg((String) arg.jjtGetValue()));

        final List<IStatement> body = new FilteredVisitor<>(IStatement.class).children(decl);
        for (IStatement statement : body) {
            builder.statment(statement);
        }
        return builder.build();
    }

    private CompiledClass compile(final ClassDeclaration decl) {
        return null;
    }
}
