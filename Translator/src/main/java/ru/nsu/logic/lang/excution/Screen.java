package ru.nsu.logic.lang.excution;

import ru.nsu.logic.lang.excution.common.IScreen;
import ru.nsu.logic.lang.grammar.common.IStatement;

public class Screen implements IScreen {
    @Override
    public void print(IStatement statement) {
        System.out.println(statement.toString());
    }
}
