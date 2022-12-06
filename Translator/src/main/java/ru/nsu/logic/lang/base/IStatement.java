package ru.nsu.logic.lang.base;

public interface IStatement {

    /**
     * Checks is statement can be executed in-place
     * (without creating new cell on virtual machine pipeline)
     */
    boolean executedInPlace();
}
