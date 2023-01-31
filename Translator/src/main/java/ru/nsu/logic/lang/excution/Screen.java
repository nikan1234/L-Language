package ru.nsu.logic.lang.excution;

import ru.nsu.logic.lang.base.execution.IScreen;
import ru.nsu.logic.lang.base.grammar.IStatement;

public class Screen implements IScreen {
    @Override
    public void print(IStatement statement) {
        System.out.println(statement.toString());
    }
}
