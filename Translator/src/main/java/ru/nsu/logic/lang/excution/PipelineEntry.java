package ru.nsu.logic.lang.excution;

import org.apache.commons.lang3.RandomStringUtils;
import ru.nsu.logic.lang.base.execution.ExecutionException;
import ru.nsu.logic.lang.base.execution.IPipelineEntry;
import ru.nsu.logic.lang.base.grammar.IStatement;

import java.util.*;

public class PipelineEntry implements IPipelineEntry {
    private final Map<String, IStatement> varInitializers;
    private final Stack<String> tempVariables;

    private final List<IStatement> statements;
    private int currentStatementIndex;

    public PipelineEntry(final Map<String, IStatement> varInitializers,
                         final List<IStatement> statements) {
        this.varInitializers = new HashMap<>(varInitializers);
        this.tempVariables = new Stack<>();

        this.statements = new ArrayList<>(statements);
        this.currentStatementIndex = 0;
    }

    @Override
    public void initializeVariable(final String varName, final IStatement value) {
        varInitializers.put(varName, value);
    }

    @Override
    public IStatement getInitializedVariable(final String varName) throws ExecutionException {
        if (!varInitializers.containsKey(varName))
            throw new ExecutionException("Cannot use not initialized variable " + varName);
        return varInitializers.get(varName);
    }

    @Override
    public String pushTempVariable() {
        int minimumSize = 4;

        while (true) {
            final String randomStr = RandomStringUtils.randomAlphabetic(minimumSize);
            if (!varInitializers.containsKey(randomStr)) {
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
