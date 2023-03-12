package ru.nsu.logic.lang.execution;

import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.common.IScreen;

import java.util.List;

public class Screen implements IScreen {

    @Override
    public void print(List<IStatement> statements) {

        final StringBuilder toPrint = new StringBuilder();
        for (int i = 0; i < statements.size() - 1; ++i) {
            toPrint.append(statements.get(i).toString()).append(' ');
        }
        if (!statements.isEmpty())
            toPrint.append(statements.get(statements.size() - 1));
        System.out.println(toPrint);
    }
}
