package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.logic.lang.excution.common.ExecutionException;
import ru.nsu.logic.lang.excution.common.IVirtualMachine;
import ru.nsu.logic.lang.grammar.common.FileLocation;
import ru.nsu.logic.lang.grammar.common.IStatement;

import java.util.ArrayList;
import java.util.List;

public class ListValue extends SimpleNode implements IStatement {
    @Getter
    @Setter
    private List<IStatement> elements;

    public ListValue(int i) {
        super(i);
    }

    public ListValue(final FileLocation location,
                     final List<IStatement> elements) {
        super(location);
        this.elements = elements;
    }

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
        return new ExecutionResult<>(new ListValue(getLocation(), executed), completed);
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
