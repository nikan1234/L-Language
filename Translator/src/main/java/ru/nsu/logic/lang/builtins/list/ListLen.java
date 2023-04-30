package ru.nsu.logic.lang.builtins.list;

import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.builtins.common.Builtin;
import ru.nsu.logic.lang.builtins.common.BuiltinClass;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.compilation.statements.ListValueStatement;
import ru.nsu.logic.lang.compilation.statements.NullValueStatement;
import ru.nsu.logic.lang.compilation.statements.NumberValueStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;

import java.util.List;

@BuiltinClass(name = "len")
public class ListLen extends Builtin {
    @Override
    public IStatement evaluate(final FileLocation location, final List<IStatement> arguments) throws ExecutionException {
        assertArgumentCount(arguments, 1);

        if (arguments.get(0) instanceof NullValueStatement)
            return new NumberValueStatement(0, location);

        final ListValueStatement list = asType(arguments.get(0), ListValueStatement.class);
        return new NumberValueStatement(list.getElements().size(), location);
    }
}
