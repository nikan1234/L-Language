package ru.nsu.logic.lang.builtins.common;

import ru.nsu.logic.lang.excution.common.ExecutionException;
import ru.nsu.logic.lang.excution.common.IVirtualMachine;
import ru.nsu.logic.lang.grammar.common.IStatement;

import java.util.List;

public interface IBuiltin {
    void initialize(final IVirtualMachine machine);
    IStatement evaluate(final List<IStatement> arguments) throws ExecutionException;
}
