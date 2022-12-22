package ru.nsu.logic.lang.base.grammar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.nsu.logic.lang.base.execution.ExecutionException;
import ru.nsu.logic.lang.base.execution.IVirtualMachine;

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
