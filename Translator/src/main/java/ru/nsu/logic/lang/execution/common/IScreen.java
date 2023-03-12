package ru.nsu.logic.lang.execution.common;

import ru.nsu.logic.lang.compilation.common.IStatement;

import java.util.List;

public interface IScreen {
    void print(final List<IStatement> statements);
}
