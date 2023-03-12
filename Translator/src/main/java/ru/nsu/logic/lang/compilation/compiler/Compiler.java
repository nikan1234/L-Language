package ru.nsu.logic.lang.compilation.compiler;

import lombok.AllArgsConstructor;
import ru.nsu.logic.lang.ast.*;
import ru.nsu.logic.lang.common.AccessType;
import ru.nsu.logic.lang.common.ComparisonOperator;
import ru.nsu.logic.lang.common.LimitedQuantifier;
import ru.nsu.logic.lang.compilation.common.*;
import ru.nsu.logic.lang.compilation.statements.*;
import ru.nsu.logic.lang.compilation.statements.logic.ComparisonFormula;
import ru.nsu.logic.lang.compilation.statements.logic.IFormula;
import ru.nsu.logic.lang.common.IExecutable;
import ru.nsu.logic.lang.compilation.statements.logic.QuantifierFormula;
import ru.nsu.logic.lang.utils.FilteredVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Compiler implements ICompiler {

    @AllArgsConstructor
    private static class ASTTransformRule<T extends Node> {
        private final Class<T> astClass;
        private final Function<T, IExecutable<?>> compiler;

        boolean isApplicable(final Node node) {
            return astClass.isInstance(node);
        }

        IExecutable<?> apply(final Node node) {
            if (!isApplicable(node))
                throw new RuntimeException("Internal compilation error: wrong node type");
            return compiler.apply(astClass.cast(node));
        }
    }
    private final List<ASTTransformRule<?>> statementRules = new ArrayList<>();


    @SuppressWarnings("unchecked")
    public static Compiler create() {
        final Compiler compiler = new Compiler();

        final Function<Node, IStatement> compileStmt = node -> (IStatement) compiler.compile(node);
        final Function<Node, IFormula> compileFormula = node -> (IFormula) compiler.compile(node);

        /* Nil */
        compiler.statementRules.add(new ASTTransformRule<>(
                ASTNullValue.class,
                node -> new NullValue(node.jjtGetLocation())));

        /* Number */
        compiler.statementRules.add(new ASTTransformRule<>(
                ASTIntValue.class,
                node -> new NumberValue(node.jjtGetValueAs(Long.class), node.jjtGetLocation())));

        /* Number */
        compiler.statementRules.add(new ASTTransformRule<>(
                ASTFloatValue.class,
                node -> new NumberValue(node.jjtGetValueAs(Double.class), node.jjtGetLocation())));

        /* Variable */
        compiler.statementRules.add(new ASTTransformRule<>(
                ASTVariableStatement.class,
                node -> new VariableStatement(node.jjtGetValueAs(String.class), node.jjtGetLocation())
        ));

        /* List */
        compiler.statementRules.add(new ASTTransformRule<>(
           ASTListValue.class,
           node -> new ListValue(
                   node.jjtGetChildren().stream().map(compileStmt).collect(Collectors.toList()),
                   node.jjtGetLocation())
        ));

        /* TODO: Member */
        compiler.statementRules.add(new ASTTransformRule<>(
                ASTMemberStatement.class,
                node -> new MemberStatement(node.jjtGetValueAs(String.class), node.jjtGetLocation())
        ));

        /* Assignment */
        compiler.statementRules.add(new ASTTransformRule<>(
                ASTAssignmentStatement.class,
                node -> new AssignmentStatement(
                        compileStmt.apply(node.jjtGetChild(0)),
                        compileStmt.apply(node.jjtGetChild(1)),
                        node.jjtGetLocation())
        ));

        /* Arithmetic */
        compiler.statementRules.add(new ASTTransformRule<>(
                ASTArithmeticStatement.class,
                node -> new ArithmeticStatement(
                        node.jjtGetChildren().stream().map(compileStmt).collect(Collectors.toList()),
                        node.jjtGetValueAs(List.class),
                        node.jjtGetLocation())
        ));

        /* Function call */
        compiler.statementRules.add(new ASTTransformRule<>(
                ASTFunctionCallStatement.class,
                node -> new FunctionCallStatement(
                        node.jjtGetValueAs(String.class),
                        node.jjtGetChildren().stream().map(compileStmt).collect(Collectors.toList()),
                        node.jjtGetLocation())
        ));

        /* Cond */
        compiler.statementRules.add(new ASTTransformRule<>(
                ASTCondStatement.class,
                node -> {
                    final List<IFormula> formulas = new ArrayList<>();
                    final List<IStatement> statements = new ArrayList<>();
                    for (int i = 0; i < node.jjtGetNumChildren() - 1; ++i) {
                        if (i % 2 == 0)
                            formulas.add(compileFormula.apply(node.jjtGetChild(i)));
                        else
                            statements.add(compileStmt.apply(node.jjtGetChild(i)));
                    }
                    statements.add(compileStmt.apply(node.jjtGetChild(node.jjtGetNumChildren() - 1)));
                    return new CondStatement(formulas, statements, node.jjtGetLocation());
                }
        ));

        /* Return */
        compiler.statementRules.add(new ASTTransformRule<>(
           ASTReturnStatement.class,
           node -> new ReturnStatement(
                   compileStmt.apply(node.jjtGetChild(0)),
                   node.jjtGetLocation())
        ));

        /* Comparison formula */
        compiler.statementRules.add(new ASTTransformRule<>(
                ASTComparisonFormula.class,
                node -> new ComparisonFormula(
                        compileStmt.apply(node.jjtGetChild(0)),
                        compileStmt.apply(node.jjtGetChild(1)),
                        node.jjtGetValueAs(ComparisonOperator.class),
                        node.jjtGetLocation())
        ));

        /* Quantifier formula */
        compiler.statementRules.add(new ASTTransformRule<>(
           ASTQuantifierFormula.class,
           node -> {
               final LimitedQuantifier<Node> quantifier = node.jjtGetValueAs(LimitedQuantifier.class);
               return new QuantifierFormula(
                       new LimitedQuantifier<>(
                               quantifier.getQuantifier(),
                               quantifier.getVariable(),
                               quantifier.getSelection(),
                               compileStmt.apply(quantifier.getSelectionSource())),
                       compileFormula.apply(node.jjtGetChild(1)));
           }
        ));

        return compiler;
    }


    @Override
    public CompiledProgram compile(final ASTLLangProgram program) throws CompilationException {
        final CompilationRegistry<ICompiledClass> compiledClasses = new CompilationRegistry<>();
        final CompilationRegistry<ICompiledFunction> compiledFunctions = new CompilationRegistry<>();;

        final CompiledProgram.CompiledProgramBuilder builder = CompiledProgram.builder();
        for (int i = 0; i < program.jjtGetNumChildren(); ++i) {
            final Node node = program.jjtGetChild(i);

            if (node instanceof ASTFunctionDeclaration)
                compiledFunctions.add(compile((ASTFunctionDeclaration) node));

            else if (node instanceof ASTClassDeclaration)
                compiledClasses.add(compile((ASTClassDeclaration) node));

            else
                builder.statement((IStatement) compile(node));
        }
        return builder
                .compiledClasses(compiledClasses)
                .compiledFunctions(compiledFunctions)
                .build();
    }

    private Compiler() {}

    private IExecutable<?> compile(final Node node) {
        for (final ASTTransformRule<?> rule : statementRules) {
            if (rule.isApplicable(node))
                return rule.apply(node);
        }
        throw new RuntimeException("Internal compilation error: rule not found for " + node);
    }

    private CompiledFunction compile(final ASTFunctionDeclaration decl) {
        final CompiledFunction.CompiledFunctionBuilder builder = CompiledFunction.builder();
        builder.name(decl.jjtGetValueAs(String.class));
        builder.accessType(AccessType.PUBLIC);
        builder.location(decl.jjtGetLocation());

        new FilteredVisitor<>(ASTArgumentDeclaration.class).children(decl)
                .forEach(arg -> builder.arg(arg.jjtGetValueAs(String.class)));

        final List<ASTBodyDeclaration> body = new FilteredVisitor<>(ASTBodyDeclaration.class).children(decl);
        assert (body.size() == 1);

        for (final Node node : body.get(0).jjtGetChildren())
            builder.statement((IStatement) compile(node));
        return builder.build();
    }

    private CompiledClass compile(final ASTClassDeclaration decl) {
        return null;
    }
}
