package ru.nsu.logic.lang.builtins.common;

import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

import java.util.List;

public abstract class Builtin implements IBuiltin {
    private IVirtualMachine machine;

    @Override
    public void initialize(final IVirtualMachine machine) {
        this.machine = machine;
    }

    protected static void assertArgumentCount(final List<IStatement> args,
                                              final int expected) throws ExecutionException{
        if (expected != args.size())
            throw new ExecutionException(
                    "Wrong argument count. " +
                    "Expected: " + expected + ". " +
                    "Actual: " + args.size());
    }

    protected static <T> void assertArgumentType(final IStatement arg,
                                                 final Class<T> targetType) throws ExecutionException {
        if (!targetType.isInstance(arg)) {
            throw new ExecutionException("Wrong argument type");
        }
    }

    protected static <T> T asType(final IStatement arg, final Class<T> type) throws ExecutionException {
        assertArgumentType(arg, type);
        return type.cast(arg);
    }

    protected IVirtualMachine getMachine() {
        return this.machine;
    }
}
