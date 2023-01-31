package ru.nsu.logic.lang.builtins.list;

import ru.nsu.logic.lang.base.execution.ExecutionException;
import ru.nsu.logic.lang.base.grammar.IStatement;
import ru.nsu.logic.lang.builtins.common.Builtin;
import ru.nsu.logic.lang.builtins.common.BuiltinClass;
import ru.nsu.logic.lang.grammar.ListValue;

import java.util.List;

@BuiltinClass(name = "cons")
public class ListCons extends Builtin {
    @Override
    public IStatement evaluate(final List<IStatement> arguments) throws ExecutionException {
        assertArgumentCount(arguments, 2);

        final List<IStatement> tail = asType(arguments.get(1), ListValue.class).getElements();
        tail.add(0, arguments.get(0));
        return new ListValue(tail, IStatement.GENERATED_STATEMENT_ID);
    }
}
