package ru.nsu.logic.lang.builtins.list;

import ru.nsu.logic.lang.excution.common.ExecutionException;
import ru.nsu.logic.lang.grammar.common.IStatement;
import ru.nsu.logic.lang.builtins.common.Builtin;
import ru.nsu.logic.lang.builtins.common.BuiltinClass;
import ru.nsu.logic.lang.grammar.ListValue;
import ru.nsu.logic.lang.grammar.NumberValue;

import java.util.List;

@BuiltinClass(name = "nth")
public class ListNth extends Builtin {
    @Override
    public IStatement evaluate(final List<IStatement> arguments) throws ExecutionException {
        assertArgumentCount(arguments, 2);
        final ListValue list = asType(arguments.get(0), ListValue.class);
        final NumberValue value = asType(arguments.get(1), NumberValue.class);
        return list.getElements().get((int)value.asInt());
    }
}
