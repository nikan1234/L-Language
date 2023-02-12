package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import ru.nsu.logic.lang.excution.common.ExecutionException;
import ru.nsu.logic.lang.excution.common.IVirtualMachine;
import ru.nsu.logic.lang.grammar.common.IFormula;

public class BooleanValue implements IFormula {
    @Getter
    private final Boolean value;

    public BooleanValue(final boolean value) {
        this.value = value;
    }

    @Override
    public ExecutionResult<IFormula> execute(IVirtualMachine machine) throws ExecutionException {
        return new ExecutionResult<>(this,true);
    }
}
