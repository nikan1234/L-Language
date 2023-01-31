package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.logic.lang.base.execution.ExecutionException;
import ru.nsu.logic.lang.base.execution.IVirtualMachine;
import ru.nsu.logic.lang.base.grammar.IStatement;

import java.util.ArrayList;
import java.util.List;

public class ListValue extends SimpleNode implements IStatement {
    @Getter
    @Setter
    private List<IStatement> elements;

    public ListValue(int i) {
        super(i);
    }

    public ListValue(LStatement p, int i) {
        super(p, i);
    }

    public ListValue(final List<IStatement> elements, final int i) {
        super(i);
        this.elements = elements;
    }

    @Override
    public ExecutionResult execute(final IVirtualMachine machine) throws ExecutionException {
        final List<IStatement> executed = new ArrayList<>(elements);

        boolean completed = true;
        for (int i = 0; i < elements.size(); ++i) {
            final IStatement element = elements.get(i);
            final boolean shouldBreak = !element.executedInPlace();

            executed.set(i, element.execute(machine).getStatement());
            if (shouldBreak) {
                completed = false;
                break;
            }
        }
        return new ExecutionResult(new ListValue(executed, id), completed);
    }

    @Override
    public boolean executedInPlace() {
        return elements.stream().allMatch(IStatement::executedInPlace);
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
