package ru.nsu.logic.lang.compilation.statements;

import lombok.*;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@EqualsAndHashCode
public class ListValue implements IStatement {
    @With
    @Getter
    private List<IStatement> elements;
    @With
    @Getter
    @EqualsAndHashCode.Exclude
    private final FileLocation location;


    @Override
    public ExecutionResult<IStatement> execute(final IVirtualMachine machine) throws ExecutionException {
        final List<IStatement> executed = new ArrayList<>(elements);

        boolean completed = true;
        for (int i = 0; i < elements.size(); ++i) {
            final IStatement element = elements.get(i);
            final ExecutionResult<IStatement> elementExecuted = element.execute(machine);
            executed.set(i, elementExecuted.getValue());

            if (!elementExecuted.isCompleted()) {
                completed = false;
                break;
            }
        }
        return new ExecutionResult<>(new ListValue(executed, getLocation()), completed);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < elements.size() - 1; ++i)
            result.append(elements.get(i).toString()).append(", ");

        if (!elements.isEmpty())
            result.append(elements.get(elements.size() - 1).toString());
        return "[" + result + "]";
    }
}
