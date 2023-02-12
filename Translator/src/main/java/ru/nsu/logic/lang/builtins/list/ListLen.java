package ru.nsu.logic.lang.builtins.list;

import ru.nsu.logic.lang.builtins.common.Builtin;
import ru.nsu.logic.lang.builtins.common.BuiltinClass;
import ru.nsu.logic.lang.excution.common.ExecutionException;
import ru.nsu.logic.lang.grammar.ListValue;
import ru.nsu.logic.lang.grammar.NumberValue;
import ru.nsu.logic.lang.grammar.common.IStatement;

import java.util.List;

@BuiltinClass(name = "len")
public class ListLen extends Builtin {
    @Override
    public IStatement evaluate(final List<IStatement> arguments) throws ExecutionException {
        assertArgumentCount(arguments, 1);
        final ListValue list = asType(arguments.get(0), ListValue.class);
        return new NumberValue(null, list.getElements().size());
    }
}
