package ru.nsu.logic.lang.compilation.statements;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

import java.util.List;


@AllArgsConstructor
public class NestedStatementSequence implements IStatement {
    @Getter
    private final List<IStatement> body;
    @With
    @Getter
    private final FileLocation location;


    @Override
    public ExecutionResult<IStatement> execute(final IVirtualMachine machine) throws ExecutionException {
        return uncompleted(machine.onPipelineExtend(this));
    }
}
