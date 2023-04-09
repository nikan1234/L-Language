package ru.nsu.logic.lang.execution;

import ru.nsu.logic.lang.compilation.common.*;
import ru.nsu.logic.lang.compilation.compiler.CompiledProgram;
import ru.nsu.logic.lang.compilation.statements.*;
import ru.nsu.logic.lang.execution.common.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VirtualMachine implements IVirtualMachine {

    private final ICompilationRegistry<ICompiledClass> compiledClasses;
    private final ICompilationRegistry<ICompiledFunction> compiledFunctions;
    private final IScreen screen = new Screen();
    private final IPipeline pipeline = new Pipeline();

    public static VirtualMachine create(final CompiledProgram compiledProgram) {
        final VirtualMachine machine = new VirtualMachine(
                compiledProgram.getCompiledClasses(),
                compiledProgram.getCompiledFunctions());

        machine.getPipeline().pushEntry(
                new PipelineEntry(Context.CreateForGlobal(), new HashMap<>(), compiledProgram.getStatements()));

        return machine;
    }

    private VirtualMachine(final ICompilationRegistry<ICompiledClass> compiledClasses,
                           final ICompilationRegistry<ICompiledFunction> compiledFunctions) {
        this.compiledClasses = compiledClasses;
        this.compiledFunctions = compiledFunctions;
    }


    @Override
    public IScreen getScreen() {
        return screen;
    }

    @Override
    public IPipeline getPipeline() {
        return pipeline;
    }

    @Override
    public void run() throws ExecutionException {
        IContext context = null;
        try {
            while (!pipeline.empty()) {
                final IPipelineEntry currentEntry = pipeline.getCurrentEntry();
                if (currentEntry.completed()) {
                    pipeline.popEntry();
                    continue;
                }
                context = pipeline.getCurrentContext();
                final IStatement statement = currentEntry.getCurrentStatement();
                final IStatement.ExecutionResult<IStatement> result = statement.execute(this);
                currentEntry.setCurrentStatement(result.getValue());
                if (result.isCompleted())
                    currentEntry.nextStatement();
            }
        }
        catch (final ExecutionException e) {
            if (context != null)
                throw new ExecutionException(
                        "Error at " + context.getLocation().getRow() +
                                ":" + context.getLocation().getColumn() +
                                ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void onPipelineRollback(final IStatement statement) {
        /// Remove entry from execution stack
        pipeline.popEntry();
        if (pipeline.getCurrentEntry().hasTempVariable())
            pipeline.getCurrentEntry().initializeVariable(
                    pipeline.getCurrentEntry().popTempVariable(),
                    statement);
    }

    @Override
    public IStatement onPipelineExtend(final IStatement statement) throws ExecutionException {
        // foo();
        if (statement instanceof FunctionCallStatement)
            return extendPipelineOnFunctionCall((FunctionCallStatement) statement);

        // foo.bar()
        else if (statement instanceof MethodCallStatement)
            return extendPipelineOnMethodCall((MethodCallStatement) statement);

        // new Foo();
        else if (statement instanceof ConstructorCallStatement)
            return extendPipelineOnCtorCall((ConstructorCallStatement) statement);

        throw new RuntimeException("Internal VM error: cannot extend pipeline for " + statement);
    }

    private IStatement extendPipelineOnFunctionCall(final FunctionCallStatement callStmt) throws ExecutionException {
        final String functionName = callStmt.getFunctionName();
        final IContext context = pipeline.getCurrentContext();

        if (context.isFunctionCtx() && context.getFunctionCtx().getFunctionName().equals(functionName))
            throw new ExecutionException("Found recursion in " + callStmt.getFunctionName());

        final Optional<ICompiledFunction> function = compiledFunctions.lookup(functionName);
        if (!function.isPresent())
            throw new ExecutionException("Function not found: " + functionName);
        if (function.get().getLocation().compareTo(context.getLocation()) > 0)
            throw new ExecutionException("Function " + functionName + " is not declared yet");

        return extendPipeline(callStmt,
                Context.CreateForFunction(functionName),
                prepareArgs(function.get().getArguments(), callStmt.getCallParameters(), functionName),
                function.get().getBody());
    }

    private IStatement extendPipelineOnMethodCall(final MethodCallStatement callStmt) throws ExecutionException {
        final String objectName = callStmt.getObjectName();
        final String methodName = callStmt.getMethodName();
        final IContext context = pipeline.getCurrentContext();

        final IStatement object = pipeline.getCurrentEntry().getInitializedVariable(objectName);
        if (!(object instanceof ObjectValue))
            throw new RuntimeException(objectName + " refers to an non-object: " + object);

        final String className = ((ObjectValue) object).getClassName();
        final Optional<ICompiledClass> compiledClass = compiledClasses.lookup(className);
        if (!compiledClass.isPresent())
            throw new ExecutionException("Class not found: " + className);

        final ICompiledMethod method = compiledClass.get().getMethod(methodName);
        if (method == null)
            throw new ExecutionException("Method not found: " + methodName);
        if (method.getLocation().compareTo(context.getLocation()) > 0)
            throw new ExecutionException("Function " + methodName + " is not declared yet");
        if (!callStmt.getAccessMask().contains(method.getAccessType()))
            throw new ExecutionException("Cannot access " + methodName + " which declared " + method.getAccessType());

        final String diagnosticMsg = className + '.' + methodName;
        final IStatement retVal = extendPipeline(callStmt,
                Context.CreateForClassMethod(className, methodName),
                prepareArgs(method.getArguments(), callStmt.getCallParameters(), diagnosticMsg), method.getBody());

        pipeline.getCurrentEntry().initializeVariable("this", object);
        return retVal;
    }

    private IStatement extendPipelineOnCtorCall(final ConstructorCallStatement createStmt) throws ExecutionException {
        final String className = createStmt.getClassName();
        final IContext context = pipeline.getCurrentContext();

        final Optional<ICompiledClass> compiledClass = compiledClasses.lookup(className);
        if (!compiledClass.isPresent())
            throw new ExecutionException("Class not found: " + createStmt.getClassName());

        if (compiledClass.get().getLocation().compareTo(context.getLocation()) > 0)
            throw new ExecutionException("Class " + compiledClass + " is not declared yet");

        final ICompiledMethod constructor = compiledClass.get().getConstructor();
        if (constructor == null)
            /// Default constructor, pipeline not extended actually
            return new ObjectValue(compiledClass.get(), createStmt.getLocation());

        if (context.isClassMethodCtx() &&
            context.getClassMethodCtx().getClassName().equals(className) &&
            context.getClassMethodCtx().getMethodName().equals(ICompiledClass.CTOR_NAME))
            throw new ExecutionException("Found recursion in " + className + " constructor");

        final ObjectValue object = new ObjectValue(compiledClass.get(), createStmt.getLocation());

        /// "Trick" to return value from constructor
        final List<IStatement> body = new LinkedList<>(constructor.getBody());
        body.add(new ReturnStatement(
                object, body.isEmpty() ? constructor.getLocation() : body.get(body.size() - 1).getLocation()));

        final String diagnosticMsg = className + '.' + ICompiledClass.CTOR_NAME;
        final IStatement retVal = extendPipeline(createStmt,
                Context.CreateForClassMethod(className, ICompiledClass.CTOR_NAME),
                prepareArgs(constructor.getArguments(), createStmt.getCallParameters(), diagnosticMsg), body);

        pipeline.getCurrentEntry().initializeVariable("this", object);
        return retVal;
    }

    private IStatement extendPipeline(final IStatement initiator,
                                      final Context context,
                                      final Map<String, IStatement> varInitializers,
                                      final List<IStatement> statements) {

        /// TODO: заменить эту залупу на пацанский log4j
        ///System.out.println("[INFO] Extended pipeline for " + context + " from " + pipeline.getCurrentContext());

        /// False if function result is not assigned to any var
        final boolean functionResultUnused = isCallResultUnused(initiator);
        final String uniqueName = functionResultUnused ? null : pipeline.getCurrentEntry().pushTempVariable();

        final IPipelineEntry entry = new PipelineEntry(context, varInitializers, statements);
        pipeline.pushEntry(entry);
        return functionResultUnused ? null : new VariableStatement(uniqueName, initiator.getLocation());
    }

    private boolean isCallResultUnused(final IStatement callStmt) {
        final IStatement topLevelStmt = pipeline.getCurrentEntry().getCurrentStatement();
        if (topLevelStmt instanceof FunctionCallStatement ||
            topLevelStmt instanceof MethodCallStatement ||
            topLevelStmt instanceof ConstructorCallStatement)
            return callStmt.equals(topLevelStmt);

        return false;
    }

    private Map<String, IStatement> prepareArgs(final List<String> argNames,
                                                final List<IStatement> callParameters,
                                                final String diagnosticName) throws ExecutionException {

        if (argNames.size() != callParameters.size())
            throw new ExecutionException(
                    "Wrong number of parameters in " + diagnosticName + ". Expected: " + argNames.size());

        return IntStream.range(0, argNames.size()).boxed()
                .collect(Collectors.toMap(argNames::get, callParameters::get));
    }
}
