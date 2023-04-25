package ru.nsu.logic.lang.compilation.statements;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.With;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

import java.util.List;

@EqualsAndHashCode
@AllArgsConstructor
public class ConstructorCallStatement implements IStatement {
    @Getter
    private String className;
    @Getter
    @EqualsAndHashCode.Exclude
    private List<IStatement> callParameters;
    @With
    @Getter
    private final FileLocation location;

    @Override
    public ExecutionResult<IStatement> execute(final IVirtualMachine machine) throws ExecutionException {
        final IStatement retVal = machine.onPipelineExtend(this);

        final boolean completed = retVal == null || retVal instanceof ObjectValueStatement;
        return new ExecutionResult<>(retVal, completed);
    }
}
