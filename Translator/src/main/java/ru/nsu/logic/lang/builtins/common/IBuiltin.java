package ru.nsu.logic.lang.builtins.common;

import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

import java.util.List;


public interface IBuiltin {
    void initialize(final IVirtualMachine machine);
    IStatement evaluate(final FileLocation location, final List<IStatement> arguments) throws ExecutionException;
}
