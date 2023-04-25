package ru.nsu.logic.lang.execution;

import ru.nsu.logic.lang.compilation.common.*;
import ru.nsu.logic.lang.compilation.statements.*;
import ru.nsu.logic.lang.execution.common.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

abstract class VirtualMachineBase implements IVirtualMachine {

    private final ICompilationRegistry<ICompiledClass> compiledClasses;
    private final ICompilationRegistry<ICompiledFunction> compiledFunctions;
    private final IScreen screen = new Screen();
    private final IPipeline pipeline = new Pipeline();


    protected VirtualMachineBase(final ICompiledProgram compiledProgram) {
        this.compiledClasses = compiledProgram.getCompiledClasses();
        this.compiledFunctions = compiledProgram.getCompiledFunctions();
    }

    protected abstract void initializePipeline(final IPipeline pipeline) throws ExecutionException;
    protected void shutdown() throws ExecutionException {}

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
            initializePipeline(pipeline);

            while (!pipeline.empty()) {
                final IPipelineEntry currentEntry = pipeline.getCurrentEntry();
                if (currentEntry.completed()) {
                    onPipelineRollback(new NullValueStatement(null));
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
        finally {
            shutdown();
        }
    }

    @Override
    public void onPipelineRollback(final IStatement statement) {
        /// Remove entry from execution stack
        pipeline.popEntry();
        if (pipeline.empty())
            return;

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
        if (!(object instanceof ObjectValueStatement))
            throw new RuntimeException(objectName + " refers to an non-object: " + object);

        final ICompiledClass compiledClass = ((ObjectValueStatement) object).myClass();
        final Optional<ICompiledMethod> method = compiledClass.getMethod(methodName);

        if (!method.isPresent())
            throw new ExecutionException("Method not found: " + methodName);
        if (method.get().getLocation().compareTo(context.getLocation()) > 0)
            throw new ExecutionException("Function " + methodName + " is not declared yet");
        if (!callStmt.getAccessMask().contains(method.get().getAccessType()))
            throw new ExecutionException("Cannot access " + methodName + " which declared " + method.get().getAccessType());

        final String diagnosticMsg = compiledClass.getName() + '.' + methodName;
        final IStatement retVal = extendPipeline(callStmt,
                Context.CreateForClassMethod(compiledClass.getName(), methodName),
                prepareArgs(method.get().getArguments(), callStmt.getCallParameters(), diagnosticMsg),
                method.get().getBody());

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

        final Optional<ICompiledMethod> constructor = compiledClass.get().getConstructor();
        if (!constructor.isPresent())
            /// Default constructor, pipeline not extended actually
            return new ObjectValueStatement(compiledClass.get(), createStmt.getLocation());

        if (context.isClassMethodCtx() &&
            context.getClassMethodCtx().getClassName().equals(className) &&
            context.getClassMethodCtx().getMethodName().equals(ICompiledClass.CTOR_NAME))
            throw new ExecutionException("Found recursion in " + className + " constructor");

        final ObjectValueStatement object = new ObjectValueStatement(compiledClass.get(), createStmt.getLocation());

        /// "Trick" to return value from constructor
        final List<IStatement> body = new LinkedList<>(constructor.get().getBody());
        body.add(new ReturnStatement(
                object, body.isEmpty() ? constructor.get().getLocation() : body.get(body.size() - 1).getLocation()));

        final String diagnosticMsg = className + '.' + ICompiledClass.CTOR_NAME;
        final IStatement retVal = extendPipeline(createStmt,
                Context.CreateForClassMethod(className, ICompiledClass.CTOR_NAME),
                prepareArgs(constructor.get().getArguments(), createStmt.getCallParameters(), diagnosticMsg), body);

        pipeline.getCurrentEntry().initializeVariable("this", object);
        return retVal;
    }

    private IStatement extendPipeline(final IStatement initiator,
                                      final Context context,
                                      final Map<String, IStatement> varInitializers,
                                      final List<IStatement> statements) {
        
        ///System.out.println("[INFO] Extended pipeline for " + context + " from " + pipeline.getCurrentContext());

        final String uniqueName = pipeline.getCurrentEntry().pushTempVariable();
        final IPipelineEntry entry = new PipelineEntry(context, varInitializers, statements);
        pipeline.pushEntry(entry);
        return new VariableStatement(uniqueName, initiator.getLocation());
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
