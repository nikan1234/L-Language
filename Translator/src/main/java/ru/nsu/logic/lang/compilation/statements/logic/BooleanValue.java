package ru.nsu.logic.lang.compilation.statements.logic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

@AllArgsConstructor
public class BooleanValue implements IFormula {
    @Getter
    private final Boolean value;

    @Override
    public ExecutionResult<IFormula> execute(IVirtualMachine machine) throws ExecutionException {
        return new ExecutionResult<>(this,true);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
