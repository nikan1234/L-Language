package ru.nsu.logic.lang.compilator;

import ru.nsu.logic.lang.grammar.ASTClassDecl;
import ru.nsu.logic.lang.grammar.ASTLLangProgram;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClassesStorage {
    private final Map<String, ASTClassDecl> classes;

    private ClassesStorage(final Map<String, ASTClassDecl> classes) {
        this.classes = classes;
    }

    public static ClassesStorage create(ASTLLangProgram program) {
        FilteredVisitor<ASTClassDecl> visitor = new FilteredVisitor<>(ASTClassDecl.class);
        return new ClassesStorage(
                visitor.children(program)
                        .stream()
                        .collect(Collectors.toMap(ASTClassDecl::getClassName, Function.identity())));
    }

    public ASTClassDecl findClass(final String className) {
        return classes.get(className);
    }
}
