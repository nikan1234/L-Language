package ru.nsu.logic.lang.grammar;

import lombok.Setter;
import ru.nsu.logic.lang.excution.common.ExecutionException;
import ru.nsu.logic.lang.excution.common.IVirtualMachine;
import ru.nsu.logic.lang.grammar.common.FileLocation;
import ru.nsu.logic.lang.grammar.common.IFormula;
import ru.nsu.logic.lang.grammar.common.IStatement;

import java.util.ArrayList;
import java.util.List;

public class CondStatement extends SimpleNode implements IStatement {
    @Setter
    private List<IFormula> formulas;
    @Setter
    private List<IStatement> statements;

    private final int currentStatementIndex;

    public CondStatement(int i) {
        super(i);
        currentStatementIndex = 0;
    }

    private CondStatement(final FileLocation location,
                          final List<IFormula> formulas,
                          final List<IStatement> statements,
                          final int currentStatementIndex) {
        super(location);
        this.formulas = formulas;
        this.statements = statements;
        this.currentStatementIndex = currentStatementIndex;
    }

    @Override
    public ExecutionResult<IStatement> execute(final IVirtualMachine machine) throws ExecutionException {
        final List<IFormula> executedFormulas = new ArrayList<>(formulas);

        for (int i = currentStatementIndex; i < formulas.size(); ++i) {
            final ExecutionResult<IFormula> formulaExecuted = executedFormulas.get(i).execute(machine);
            executedFormulas.set(i, formulaExecuted.getValue());

            if (!formulaExecuted.isCompleted())
                return new ExecutionResult<>(
                        new CondStatement(getLocation(), executedFormulas, statements, i),
                        false);

            if (asBool(formulaExecuted.getValue())) {
                return statements.get(i).execute(machine);
            }
        }
        return statements.get(statements.size() - 1).execute(machine);
    }


    private boolean asBool(final IFormula formula) throws ExecutionException {
        if (formula instanceof BooleanValue)
            return ((BooleanValue) formula).getValue();
        throw new ExecutionException("Not logical value");
    }
}
