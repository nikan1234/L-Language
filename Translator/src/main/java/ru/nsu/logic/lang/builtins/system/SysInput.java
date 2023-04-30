package ru.nsu.logic.lang.builtins.system;

import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.builtins.common.Builtin;
import ru.nsu.logic.lang.builtins.common.BuiltinClass;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.compilation.statements.NullValueStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;

import java.util.List;
import java.util.Scanner;

@BuiltinClass(name = "input")  // TODO
public class SysInput extends Builtin {
    @Override
    public IStatement evaluate(final FileLocation location, final List<IStatement> arguments) throws ExecutionException {
        Scanner in = new Scanner(System.in);

        String s = in.nextLine();
        System.out.println(s);
        return new NullValueStatement(location);
    }
}
