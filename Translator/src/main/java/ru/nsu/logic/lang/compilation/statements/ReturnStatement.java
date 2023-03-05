package ru.nsu.logic.lang.compilation.statements;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

@AllArgsConstructor
public class ReturnStatement implements IStatement {
    @Getter
    private IStatement what;
    @With
    @Getter
    private final FileLocation location;

    @Override
    public ExecutionResult<IStatement> execute(final IVirtualMachine machine) throws ExecutionException {
        final ExecutionResult<IStatement> whatExecuted = what.execute(machine);
        if (whatExecuted.isCompleted()) {
            machine.onPipelineRollback(whatExecuted.getValue());
            return new ExecutionResult<>(null, true);
        }
        return new ExecutionResult<>(new ReturnStatement(whatExecuted.getValue(), getLocation()), false);
    }
}
