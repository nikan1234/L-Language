package ru.nsu.logic.lang.grammar.common;


import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.nsu.logic.lang.excution.common.ExecutionException;
import ru.nsu.logic.lang.excution.common.IVirtualMachine;

public interface IExecutable<T extends IExecutable<T>> {
    @AllArgsConstructor
    class ExecutionResult<U> {
        @Getter
        U value;
        @Getter
        boolean completed;
    }

    /**
     * Executes statement
     */
    ExecutionResult<T> execute(final IVirtualMachine machine) throws ExecutionException;
}
