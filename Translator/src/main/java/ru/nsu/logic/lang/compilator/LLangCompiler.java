package ru.nsu.logic.lang.compilator;

import ru.nsu.logic.lang.grammar.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LLangCompiler {

    private LLangProgram.Unit visit(final ASTParamVariableDecl decl) {
        return new LLangProgram.SingleUnit(decl.getParamName());
    }

    private LLangProgram.Unit visit(final ASTClassMethodDecl decl) {
        List<ASTParamVariableDecl> args = new FilteredVisitor<>(ASTParamVariableDecl.class).children(decl);
        return new LLangProgram.ListUnit(Arrays.asList(
                new LLangProgram.SingleUnit(decl.getName()),
                new LLangProgram.SingleUnit(decl.getAccessType().name()),
                new LLangProgram.ListUnit(args.stream().map(this::visit).collect(Collectors.toList()))
        ));
    }

    private LLangProgram.Unit visit(final ASTClassMemberDecl decl) {
        return new LLangProgram.ListUnit(Arrays.asList(
                        new LLangProgram.SingleUnit(decl.getName()),
                        new LLangProgram.SingleUnit(decl.getAccessType().name())
        ));
    }

    private LLangProgram.Unit visit(final ASTClassDecl decl) {
        final List<ASTClassMemberDecl> members = new FilteredVisitor<>(ASTClassMemberDecl.class).children(decl);
        final List<ASTClassMethodDecl> methods = new FilteredVisitor<>(ASTClassMethodDecl.class).children(decl);
        return new LLangProgram.ListUnit(Arrays.asList(
                new LLangProgram.SingleUnit(decl.getClassName()),
                new LLangProgram.ListUnit(members.stream().map(this::visit).collect(Collectors.toList())),
                new LLangProgram.ListUnit(methods.stream().map(this::visit).collect(Collectors.toList()))
        ));
    }

    private LLangProgram.Unit visit(final ASTFunctionDecl decl) {
        List<ASTParamVariableDecl> args = new FilteredVisitor<>(ASTParamVariableDecl.class).children(decl);
        return new LLangProgram.ListUnit(Arrays.asList(
                new LLangProgram.SingleUnit(decl.getName()),
                new LLangProgram.ListUnit(args.stream().map(this::visit).collect(Collectors.toList()))
        ));
    }

    public LLangProgram compile(final ASTLLangProgram program) {
        final List<ASTClassDecl> classes = new FilteredVisitor<>(ASTClassDecl.class).children(program);
        final List<ASTFunctionDecl> functions = new FilteredVisitor<>(ASTFunctionDecl.class).children(program);

        return LLangProgram
                .builder()
                .classes(new LLangProgram.ListUnit(classes.stream().map(this::visit).collect(Collectors.toList())))
                .functions(new LLangProgram.ListUnit(functions.stream().map(this::visit).collect(Collectors.toList())))
                .build();
    }
}
