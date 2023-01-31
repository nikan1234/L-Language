package ru.nsu.logic.lang.builtins.common;

import ru.nsu.logic.lang.base.execution.ExecutionException;
import ru.nsu.logic.lang.base.execution.IVirtualMachine;
import ru.nsu.logic.lang.base.grammar.IStatement;

import java.util.List;

public interface IBuiltin {
    void initialize(final IVirtualMachine machine);
    IStatement evaluate(final List<IStatement> arguments) throws ExecutionException;
}
