package ru.nsu.logic.lang.compilation.statements;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;
import ru.nsu.logic.lang.ast.FileLocation;

@AllArgsConstructor
public class NullValue implements IStatement {

    @With
    @Getter
    private final FileLocation location;

    @Override
    public ExecutionResult<IStatement> execute(IVirtualMachine machine) throws ExecutionException {
        return new ExecutionResult<>(this, true);
    }

    @Override
    public String toString() {
        return "nil";
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof NullValue;
    }

    @Override
    public int hashCode() {
        return NullValue.class.getName().hashCode();
    }
}
