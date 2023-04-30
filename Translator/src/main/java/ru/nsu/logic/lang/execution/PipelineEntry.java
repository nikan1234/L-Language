package ru.nsu.logic.lang.execution;

import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IContext;
import ru.nsu.logic.lang.execution.common.IPipelineEntry;

import java.util.*;

public class PipelineEntry implements IPipelineEntry {
    private final Context context;

    @Getter
    private final Map<String, IStatement> initializedVariables;
    private final Stack<String> tempVariables;

    private final List<IStatement> statements;
    private int currentStatementIndex;

    public PipelineEntry(final Context context,
                         final Map<String, IStatement> initializedVariables,
                         final List<IStatement> statements) {
        this.context = context;
        this.initializedVariables = initializedVariables;
        this.tempVariables = new Stack<>();

        this.statements = new ArrayList<>(statements);
        this.currentStatementIndex = 0;
    }

    @Override
    public IContext getContext() {
        return context.withLocation(getCurrentStatement().getLocation());
    }

    @Override
    public void initializeVariable(final String varName, final IStatement value) {
        initializedVariables.put(varName, value);
    }

    @Override
    public IStatement getInitializedVariable(final String varName) throws ExecutionException {
        if (!initializedVariables.containsKey(varName))
            if (tempVariables.contains(varName))
                throw new ExecutionException("Function returned nothing");
            else
                throw new ExecutionException("Cannot use not initialized variable " + varName);
        return initializedVariables.get(varName);
    }

    @Override
    public boolean hasTempVariable() {
        return !tempVariables.empty();
    }

    @Override
    public String pushTempVariable() {
        int minimumSize = 4;

        while (true) {
            // adding '?' symbol to prevent collision with user vars names
            final String randomStr = '?' + RandomStringUtils.randomAlphabetic(minimumSize);
            if (!initializedVariables.containsKey(randomStr) && !tempVariables.contains(randomStr)) {
                tempVariables.add(randomStr);
                return randomStr;
            }
            ++minimumSize;
        }
    }

    @Override
    public String popTempVariable() {
        return tempVariables.pop();
    }

    @Override
    public boolean completed() {
        return currentStatementIndex >= statements.size();
    }

    @Override
    public void nextStatement() {
        ++currentStatementIndex;
    }

    @Override
    public IStatement getCurrentStatement() {
        if (currentStatementIndex >= statements.size())
            throw new IndexOutOfBoundsException();
        return statements.get(currentStatementIndex);
    }

    @Override
    public void setCurrentStatement(final IStatement statement) {
        if (currentStatementIndex >= statements.size())
            throw new IndexOutOfBoundsException();
        statements.set(currentStatementIndex, statement);
    }
}
