package ru.nsu.logic.lang.execution;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import ru.nsu.logic.lang.execution.common.IContext;
import ru.nsu.logic.lang.ast.FileLocation;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Context implements IContext {

    @AllArgsConstructor
    private static class FunctionCtx implements IContext.IFunctionCtx {
        @Getter String functionName;
    }

    @AllArgsConstructor
    private static class ClassMethodCtx implements IContext.IClassMethodCtx {
        @Getter String className;
        @Getter String methodName;
    }

    @With
    private final FileLocation location;
    private FunctionCtx functionCxt;
    private ClassMethodCtx classMethodCtx;

    public static Context CreateForGlobal() {
        return new Context(null, null, null);
    }

    public static Context CreateForFunction(final String function) {
        return new Context(null, new FunctionCtx(function), null);
    }

    public static Context CreateForClassMethod(final String className, final String methodName) {
        return new Context(null, null, new ClassMethodCtx(className, methodName));
    }

    @Override
    public FileLocation getLocation() {
        return location;
    }

    @Override
    public boolean isGlobalCtx() {
        return functionCxt == null && classMethodCtx == null;
    }

    @Override
    public boolean isFunctionCtx() {
        return functionCxt != null;
    }

    @Override
    public boolean isClassMethodCtx() {
        return classMethodCtx != null;
    }

    @Override
    public IFunctionCtx getFunctionCtx() {
        return functionCxt;
    }

    @Override
    public IClassMethodCtx getClassMethodCtx() {
        return classMethodCtx;
    }

    @Override
    public String toString() {
        if (isFunctionCtx())
            return functionCxt.getFunctionName();

        if (isClassMethodCtx())
            return classMethodCtx.getClassName() + '.' + classMethodCtx.getMethodName();

        return "global";
    }
}
