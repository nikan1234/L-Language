package ru.nsu.logic.lang.compilation.statements;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

@AllArgsConstructor
public class VariableStatement implements IStatement {
    @Getter
    private String name;
    @With
    @Getter
    private final FileLocation location;

    void setValue(final IVirtualMachine machine, final IStatement statement) {
        machine.getPipeline().getCurrentEntry().initializeVariable(name, statement);
    }

    @Override
    public ExecutionResult<IStatement> execute(IVirtualMachine machine) throws ExecutionException {
        return new ExecutionResult<>(
                machine.getPipeline().getCurrentEntry().getInitializedVariable(name),
                true);
    }
}
