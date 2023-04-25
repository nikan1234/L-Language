package ru.nsu.logic.lang.builtins.list;

import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.builtins.common.Builtin;
import ru.nsu.logic.lang.builtins.common.BuiltinClass;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.compilation.statements.ListValueStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;

import java.util.List;

@BuiltinClass(name = "cons")
public class ListCons extends Builtin {
    @Override
    public IStatement evaluate(final FileLocation location, final List<IStatement> arguments) throws ExecutionException {
        assertArgumentCount(arguments, 2);

        final List<IStatement> tail = asType(arguments.get(1), ListValueStatement.class).getElements();
        tail.add(0, arguments.get(0));
        return new ListValueStatement(tail, location);
    }
}
