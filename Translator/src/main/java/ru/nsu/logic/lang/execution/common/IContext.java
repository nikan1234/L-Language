package ru.nsu.logic.lang.execution.common;

import ru.nsu.logic.lang.ast.FileLocation;

public interface IContext {

    FileLocation getLocation();

    interface IFunctionCtx {
        String getFunctionName();
    }

    interface IClassMethodCtx {
        String getClassName();
        String getMethodName();
    }

    boolean isGlobalCtx();
    boolean isFunctionCtx();
    boolean isClassMethodCtx();

    IFunctionCtx getFunctionCtx();
    IClassMethodCtx getClassMethodCtx();
}
