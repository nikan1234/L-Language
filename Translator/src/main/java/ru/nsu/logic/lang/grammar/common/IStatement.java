package ru.nsu.logic.lang.grammar.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.nsu.logic.lang.excution.common.ExecutionException;
import ru.nsu.logic.lang.excution.common.IVirtualMachine;

public interface IStatement {
    int GENERATED_STATEMENT_ID = -1;

    @AllArgsConstructor
    class ExecutionResult {
        @Getter
        IStatement statement;
        @Getter
        boolean completed;
    }

    /**
     * Executes statement
     */
    ExecutionResult execute(final IVirtualMachine machine) throws ExecutionException;

    /**
     * Checks is statement can be executed in-place
     * (without creating new cell on virtual machine pipeline)
     */
    boolean executedInPlace();
}
