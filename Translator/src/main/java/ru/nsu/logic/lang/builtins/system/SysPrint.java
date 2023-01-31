package ru.nsu.logic.lang.builtins.system;

import ru.nsu.logic.lang.base.execution.ExecutionException;
import ru.nsu.logic.lang.base.grammar.IStatement;
import ru.nsu.logic.lang.builtins.common.Builtin;
import ru.nsu.logic.lang.builtins.common.BuiltinClass;

import java.util.List;

@BuiltinClass(name = "print")
public class SysPrint extends Builtin {
    @Override
    public IStatement evaluate(final List<IStatement> arguments) throws ExecutionException {
        assertArgumentCount(arguments, 1);
        getMachine().getScreen().print(arguments.get(0));
        return null;
    }
}
