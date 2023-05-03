package ru.nsu.logic.lang.compilation.compiler;

import lombok.AllArgsConstructor;
import ru.nsu.logic.lang.ast.*;
import ru.nsu.logic.lang.common.AccessType;
import ru.nsu.logic.lang.common.ComparisonOperator;
import ru.nsu.logic.lang.common.IExecutable;
import ru.nsu.logic.lang.common.LimitedQuantifier;
import ru.nsu.logic.lang.compilation.common.*;
import ru.nsu.logic.lang.compilation.statements.*;
import ru.nsu.logic.lang.compilation.statements.logic.ComparisonFormulaStatement;
import ru.nsu.logic.lang.compilation.statements.logic.IFormula;
import ru.nsu.logic.lang.compilation.statements.logic.QuantifierFormulaStatement;
import ru.nsu.logic.lang.utils.FilteredVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
                node -> new NullValueStatement(node.jjtGetLocation())));

        /* Number */
        compiler.statementRules.add(new ASTTransformRule<>(
                ASTIntValue.class,
                node -> new NumberValueStatement(node.jjtGetValueAs(Long.class), node.jjtGetLocation())));

        /* Number */
        compiler.statementRules.add(new ASTTransformRule<>(
                ASTFloatValue.class,
                node -> new NumberValueStatement(node.jjtGetValueAs(Double.class), node.jjtGetLocation())));

        /* List */
        compiler.statementRules.add(new ASTTransformRule<>(
           ASTListValue.class,
           node -> new ListValueStatement(
                   node.jjtGetChildren().stream().map(compileStmt).collect(Collectors.toList()),
                   node.jjtGetLocation())
        ));

        /* Variable */
        compiler.statementRules.add(new ASTTransformRule<>(
                ASTVariableStatement.class,
                node -> new VariableStatement(node.jjtGetValueAs(String.class), node.jjtGetLocation())
        ));

        /* Member */
        compiler.statementRules.add(new ASTTransformRule<>(
                ASTMemberStatement.class,
                node -> new MemberStatement(
                        node.jjtGetValueAs(String.class).split("\\.")[0],
                        node.jjtGetValueAs(String.class).split("\\.")[1],
                        node.jjtGetLocation())
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

        /* Method call */
        compiler.statementRules.add(new ASTTransformRule<>(
                ASTMethodCallStatement.class,
                node -> new MethodCallStatement(
                        node.jjtGetValueAs(String.class).split("\\.")[0],
                        node.jjtGetValueAs(String.class).split("\\.")[1],
                        node.jjtGetChildren().stream().map(compileStmt).collect(Collectors.toList()),
                        node.jjtGetLocation())
        ));

        /* Object create */
        compiler.statementRules.add(new ASTTransformRule<>(
                ASTConstructorCallStatement.class,
                node -> new ConstructorCallStatement(
                        node.jjtGetValueAs(String.class),
                        node.jjtGetChildren().stream().map(compileStmt).collect(Collectors.toList()),
                        node.jjtGetLocation())
        ));

        /* Base constructor call */
        compiler.statementRules.add(new ASTTransformRule<>(
                ASTBaseConstructorCallStatement.class,
                node -> new BaseConstructorCallStatement(
                        node.jjtGetChildren().stream().map(compileStmt).collect(Collectors.toList()),
                        node.jjtGetLocation())
        ));

        /* Nested statement */
        compiler.statementRules.add(new ASTTransformRule<>(
                ASTNestedStatementSequence.class,
                node -> new NestedStatementSequence(
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

        /* Iteration */
        compiler.statementRules.add(new ASTTransformRule<>(
           ASTIterationStatement.class,
           node -> new IterationStatement(
                   (AssignmentStatement) compileStmt.apply(node.jjtGetChild(1)),
                   compileStmt.apply(node.jjtGetChild(0)),
                   compileStmt.apply(node.jjtGetChild(3)),
                   compileFormula.apply(node.jjtGetChild(2)),
                   node.jjtGetLocation()
           )
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
                node -> new ComparisonFormulaStatement(
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
               return new QuantifierFormulaStatement(
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
                compiledClasses.add(compile((ASTClassDeclaration) node, compiledClasses));

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


    /// Compile functions
    private ICompiledFunction compile(final ASTFunctionDeclaration decl) throws CompilationException {
        final CompiledFunction.CompiledFunctionBuilder builder = CompiledFunction.builder();
        builder.name(decl.jjtGetValueAs(String.class));
        builder.location(decl.jjtGetLocation());

        new FilteredVisitor<>(ASTArgumentDeclaration.class).children(decl)
                .forEach(arg -> builder.arg(arg.jjtGetValueAs(String.class)));

        findBody(decl).jjtGetChildren()
                .forEach(node -> builder.statement((IStatement) compile(node)));
        return builder.build();
    }

    /// Compile methods
    private ICompiledMethod compile(final ICompiledClass owner, final Node methodOrConstructor)
            throws CompilationException {

        final CompiledClass.Method.MethodBuilder builder = CompiledClass.Method.builder();
        builder.owner(owner);
        builder.name(methodOrConstructor.jjtGetValueAs(String.class));
        builder.location(methodOrConstructor.jjtGetLocation());

        new FilteredVisitor<>(ASTArgumentDeclaration.class).children(methodOrConstructor)
                .forEach(arg -> builder.arg(arg.jjtGetValueAs(String.class)));

        findBody(methodOrConstructor).jjtGetChildren()
                .forEach(node -> builder.statement((IStatement) compile(node)));

        builder.accessType(findAccessType(methodOrConstructor, AccessType.PROTECTED));
        return builder.build();
    }

    /// Compile classes
    private ICompiledClass compile(final ASTClassDeclaration decl,
                                   final CompilationRegistry<ICompiledClass> compiledClasses)
            throws CompilationException {

        // Base class
        ICompiledClass baseClass = null;

        final Optional<ASTInheritanceDeclaration> inheritanceDecl =
                new FilteredVisitor<>(ASTInheritanceDeclaration.class).children(decl).stream().findFirst();
        if (inheritanceDecl.isPresent()) {
            final String baseClassName = inheritanceDecl.get().jjtGetValueAs(String.class);
            final Optional<ICompiledClass> baseClassOpt = compiledClasses.lookup(baseClassName);
            if (!baseClassOpt.isPresent())
                throw new CompilationException("Base class " + baseClassName + " not found");

            baseClass = baseClassOpt.get();
        }

        final CompiledClass compiledClass = new CompiledClass(
                decl.jjtGetValueAs(String.class),
                decl.jjtGetLocation(), baseClass);

        // Members
        compiledClass.setMembers(
                new FilteredVisitor<>(ASTClassMemberDeclaration.class).children(decl).stream()
                        .map(astMember -> new CompiledClass.Member(
                                compiledClass,
                                astMember.jjtGetValueAs(String.class),
                                findAccessType(astMember, null), astMember.jjtGetLocation()))
                        .collect(Collectors.toList()));

        // Constructor
        final List<ASTClassConstructorDeclaration> constructors =
                new FilteredVisitor<>(ASTClassConstructorDeclaration.class).children(decl);
        if (constructors.size() > 1)
            throw new CompilationException(
                    "Found multiple constructors in class " + compiledClass.getName() +
                            ". First constructor: " + constructors.get(0).jjtGetLocation() +
                            " and second: " + constructors.get(1).jjtGetLocation());
        if (!constructors.isEmpty())
            compiledClass.setConstructor(compile(compiledClass, constructors.get(0)));

        // Methods
        final CompilationRegistry<ICompiledMethod> compiledMethods = new CompilationRegistry<>();
        for (final ASTClassMethodDeclaration method :
                new FilteredVisitor<>(ASTClassMethodDeclaration.class).children(decl))
            compiledMethods.add(compile(compiledClass, method));

        compiledClass.setMethods(compiledMethods);
        return compiledClass;
    }

    // ---------------- Common utils ----------------

    ASTBodyDeclaration findBody(final Node functionOrMethod) throws CompilationException {
        final List<ASTBodyDeclaration> body = new FilteredVisitor<>(ASTBodyDeclaration.class).children(functionOrMethod);
        if (body.size() != 1)
            throw new CompilationException(body.isEmpty() ? "Wrong function body" : "Ambiguous function body");
        return body.get(0);
    }

    AccessType findAccessType(final Node memberOrMethod, final AccessType defaultValue) {
        final List<ASTAccessType> access = new FilteredVisitor<>(ASTAccessType.class).children(memberOrMethod);
        if (access.isEmpty()) {
            if (defaultValue != null)
                return defaultValue;
            else
                throw new RuntimeException("Access type required");
        }
        else if (access.size() == 1)
            return access.get(0).jjtGetValueAs(AccessType.class);

        throw new RuntimeException("Ambiguous access type");
    }
}
