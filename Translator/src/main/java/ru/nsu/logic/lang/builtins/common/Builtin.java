package ru.nsu.logic.lang.builtins.common;

import ru.nsu.logic.lang.excution.common.IVirtualMachine;
import ru.nsu.logic.lang.grammar.common.IStatement;

import java.util.List;

public abstract class Builtin implements IBuiltin {
    private IVirtualMachine machine;

    @Override
    public void initialize(final IVirtualMachine machine) {
        this.machine = machine;
    }

    protected static void assertArgumentCount(final List<IStatement> args, final int expected) {
        if (expected != args.size())
            throw new IllegalArgumentException(
                    "Wrong argument count. " +
                    "Expected: " + expected + ". " +
                    "Actual: " + args.size());
    }

    protected static <T> void assertArgumentType(final IStatement arg, final Class<T> targetType) {
        if (!targetType.isInstance(arg)) {
            throw new IllegalArgumentException("Wrong argument type");
        }
    }

    protected static <T> T asType(final IStatement arg, final Class<T> type) {
        assertArgumentType(arg, type);
        return type.cast(arg);
    }

    protected IVirtualMachine getMachine() {
        return this.machine;
    }
}
