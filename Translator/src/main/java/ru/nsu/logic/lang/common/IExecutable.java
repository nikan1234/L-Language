package ru.nsu.logic.lang.common;


import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

public interface IExecutable<T extends IExecutable<T>> {
    @AllArgsConstructor
    class ExecutionResult<U> {
        @Getter
        U value;
        @Getter
        boolean completed;
    }

    default ExecutionResult<T> completed(T value) { return new ExecutionResult<>(value, true); }
    default ExecutionResult<T> uncompleted(T value) { return new ExecutionResult<>(value, false); }

    /**
     * Executes statement
     */
    ExecutionResult<T> execute(final IVirtualMachine machine) throws ExecutionException;
}
