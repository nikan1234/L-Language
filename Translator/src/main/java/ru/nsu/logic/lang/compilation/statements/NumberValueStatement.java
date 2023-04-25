package ru.nsu.logic.lang.compilation.statements;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NumberValueStatement implements IStatement {

    private final Number number;
    @With
    @Getter
    private final FileLocation location;

    public NumberValueStatement(final long l, final FileLocation location) {
        number = l;
        this.location = location;
    }

    public NumberValueStatement(final double d, final FileLocation location) {
        number = d;
        this.location = location;
    }

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
