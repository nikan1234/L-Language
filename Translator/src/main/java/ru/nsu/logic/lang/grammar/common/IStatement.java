package ru.nsu.logic.lang.grammar.common;

public interface IStatement extends IExecutable<IStatement> {
    /**
     Returns location of statement in source file
     */
    FileLocation getLocation();
}
