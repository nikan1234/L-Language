package ru.nsu.logic.lang.compilation.statements;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

@AllArgsConstructor
public class NumberValue implements IStatement {

    private Number number;

    @With
    @Getter
    private final FileLocation location;

    @Override
    public ExecutionResult<IStatement> execute(IVirtualMachine machine) {
        return new ExecutionResult<>(this, true);
    }

    @Override
    public String toString() {
        return number.toString();
    }

    public double asDouble() {
        return number.intValue();
    }

    public long asInt() throws ExecutionException {
        if (number instanceof Long)
            return number.longValue();
        throw new ExecutionException("Expected integer");
    }
}
