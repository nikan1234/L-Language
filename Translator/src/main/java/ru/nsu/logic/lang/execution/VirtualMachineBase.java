package ru.nsu.logic.lang.execution;

import ru.nsu.logic.lang.common.AccessType;
import ru.nsu.logic.lang.compilation.common.*;
import ru.nsu.logic.lang.compilation.compiler.CompiledClass;
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
        // do { ... }
        if (statement instanceof NestedStatementSequence)
            return extendPipelineOnNestedCall((NestedStatementSequence) statement);

        // foo();
        else if (statement instanceof FunctionCallStatement)
            return extendPipelineOnFunctionCall((FunctionCallStatement) statement);

        // foo.bar()
        else if (statement instanceof MethodCallStatement)
            return extendPipelineOnMethodCall((MethodCallStatement) statement);

        // new Foo();
        else if (statement instanceof ConstructorCallStatement)
            return extendPipelineOnCtorCall((ConstructorCallStatement) statement);

        throw new RuntimeException("Internal VM error: cannot extend pipeline for " + statement);
    }

    private IStatement extendPipelineOnNestedCall(final NestedStatementSequence nestedSeqStmt) {
        /* Execute nested block in the same context */
        return extendPipeline(nestedSeqStmt,
                (Context) getPipeline().getCurrentContext(),
                getPipeline().getCurrentEntry().getInitializedVariables(),
                nestedSeqStmt.getBody());
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
        if (ICompiledClass.CTOR_NAME.equals(callStmt.getMethodName()))
            throw new RuntimeException("VM internal error: unexpected constructor call");

        final String methodName = callStmt.getMethodName();
        final IContext context = pipeline.getCurrentContext();

        final IObject object = callStmt.getObject(this);
        final ICompiledClass objectClass = object.getObjectClass();
        final ICompiledMethod method = objectClass.accessMethod(methodName, callStmt.getAccessMask());

        if (method.getLocation().compareTo(context.getLocation()) > 0)
            throw new ExecutionException("Function " + methodName + " is not declared yet");
        if (context.isClassMethodCtx() &&
            context.getClassMethodCtx().getClassName().equals(objectClass.getName()) &&
            context.getClassMethodCtx().getMethodName().equals(methodName))
            throw new ExecutionException("Found recursion in " + callStmt.getMethodName());

        final String diagnosticMsg = objectClass.getName() + '.' + methodName;
        final IStatement retVal = extendPipeline(callStmt,
                Context.CreateForClassMethod(objectClass.getName(), methodName),
                prepareArgs(method.getArguments(), callStmt.getCallParameters(), diagnosticMsg),
                method.getBody());

        pipeline.getCurrentEntry().initializeVariable("this", object);
        return retVal;
    }

    private IStatement extendPipelineOnCtorCall(final ConstructorCallStatement createStmt) throws ExecutionException {
        final IContext context = pipeline.getCurrentContext();

        /// Search for class
        final Optional<ICompiledClass> classToCreate;
        if (createStmt instanceof BaseConstructorCallStatement) {
            /// Base constructor call
            final String currentClassName = context.getClassMethodCtx().getClassName();
            classToCreate = compiledClasses.lookup(currentClassName).orElseThrow(RuntimeException::new).getBase();
        }
        else
            classToCreate = compiledClasses.lookup(createStmt.getClassName());


        if (!classToCreate.isPresent())
            throw new ExecutionException("Class not found");
        if (classToCreate.get().getLocation().compareTo(context.getLocation()) > 0)
            throw new ExecutionException("Class " + classToCreate + " is not declared yet");
        if (context.isClassMethodCtx() &&
            context.getClassMethodCtx().getClassName().equals(classToCreate.get().getName()) &&
            context.getClassMethodCtx().getMethodName().equals(ICompiledClass.CTOR_NAME))
            throw new ExecutionException("Found recursion in " + classToCreate.get().getName() + " constructor");

        /// Search for any non-default constructor
        Optional<ICompiledClass> currentClass = classToCreate;
        Optional<ICompiledMethod> constructor = Optional.empty();
        while (currentClass.isPresent() && !constructor.isPresent()) {
            constructor = currentClass.get().getConstructor(createStmt.getAccessMask());
            currentClass = currentClass.get().getBase();
        }

        final IObject actualObject = createStmt instanceof BaseConstructorCallStatement
                ? (IObject) getPipeline().getCurrentEntry().getInitializedVariable("this")
                : new ObjectValueStatement(classToCreate.get(), createStmt.getLocation());

        if (!constructor.isPresent())
            /// Default constructor, pipeline not extended actually
            return actualObject;

        final List<IStatement> body = new LinkedList<>(constructor.get().getBody());

        /// "Trick" to call base class constructor"
        if (constructor.get().getOwner().getBase().isPresent()
                && body.stream().noneMatch(s -> s instanceof BaseConstructorCallStatement))
            body.add(0, new BaseConstructorCallStatement(body.isEmpty()
                    ? constructor.get().getLocation()
                    : body.get(0).getLocation()));

        /// "Trick" to return value from constructor
        body.add(new ReturnStatement(actualObject, body.isEmpty()
                ? constructor.get().getLocation()
                : body.get(body.size() - 1).getLocation()));

        final String diagnosticMsg = constructor.get().getOwner().getName() + '.' + ICompiledClass.CTOR_NAME;
        final IStatement retVal = extendPipeline(createStmt,
                Context.CreateForClassMethod(constructor.get().getOwner().getName(), ICompiledClass.CTOR_NAME),
                prepareArgs(constructor.get().getArguments(), createStmt.getCallParameters(), diagnosticMsg), body);

        /// We found non-default constructor, but it could be superclass constructor, so do down-cast
        pipeline.getCurrentEntry().initializeVariable("this", actualObject.toBase(constructor.get().getOwner()));
        return retVal;
    }

    private IStatement extendPipeline(final IStatement initiator,
                                      final Context context,
                                      final Map<String, IStatement> varInitializers,
                                      final List<IStatement> statements) {
        
        System.out.println("[INFO] Extended pipeline for " + context + " from " + pipeline.getCurrentContext());

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
