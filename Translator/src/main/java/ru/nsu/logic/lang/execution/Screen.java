package ru.nsu.logic.lang.execution;

import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.common.IScreen;

public class Screen implements IScreen {
    @Override
    public void print(IStatement statement) {
        System.out.println(statement.toString());
    }
}
