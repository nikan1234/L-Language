package ru.nsu.logic.lang.builtins.list;

import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.builtins.common.Builtin;
import ru.nsu.logic.lang.builtins.common.BuiltinClass;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.compilation.statements.ListValue;
import ru.nsu.logic.lang.compilation.statements.NumberValue;
import ru.nsu.logic.lang.execution.common.ExecutionException;

import java.util.List;

@BuiltinClass(name = "nth")
public class ListNth extends Builtin {
    @Override
    public IStatement evaluate(final FileLocation location, final List<IStatement> arguments) throws ExecutionException {
        assertArgumentCount(arguments, 2);

        final ListValue list = asType(arguments.get(0), ListValue.class);
        final int idx = (int) asType(arguments.get(1), NumberValue.class).asInt();
        return list.getElements().get(idx).withLocation(location);
    }
}
