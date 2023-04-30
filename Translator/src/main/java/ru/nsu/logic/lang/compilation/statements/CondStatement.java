package ru.nsu.logic.lang.compilation.statements;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.With;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.compilation.statements.logic.BooleanValueStatement;
import ru.nsu.logic.lang.compilation.statements.logic.IFormula;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

import java.util.ArrayList;
import java.util.List;


public class CondStatement implements IStatement {
    @With(AccessLevel.PRIVATE)
    private final List<IFormula> formulas;
    private final List<IStatement> statements;
    private final int currentStatementIndex;

    @With
    @Getter
    private final FileLocation location;

    public CondStatement(final List<IFormula> formulas,
                         final List<IStatement> statements,
                         final FileLocation location) {
        this(formulas, statements, 0, location);
    }

    private CondStatement(final List<IFormula> formulas,
                          final List<IStatement> statements,
                          final int currentStatementIndex,
                          final FileLocation location) {
        this.formulas = formulas;
        this.statements = statements;
        this.currentStatementIndex = currentStatementIndex;
        this.location = location;
    }


    @Override
    public ExecutionResult<IStatement> execute(final IVirtualMachine machine) throws ExecutionException {
        final List<IFormula> executedFormulas = new ArrayList<>(formulas);

        for (int i = currentStatementIndex; i < formulas.size(); ++i) {
            final ExecutionResult<IFormula> formulaExecuted = executedFormulas.get(i).execute(machine);
            executedFormulas.set(i, formulaExecuted.getValue());

            if (!formulaExecuted.isCompleted())
                return uncompleted(withFormulas(executedFormulas));
            
            if (asBool(formulaExecuted.getValue())) {
                return statements.get(i).execute(machine);
            }
        }
        return statements.get(statements.size() - 1).execute(machine);
    }


    private boolean asBool(final IFormula formula) throws ExecutionException {
        if (formula instanceof BooleanValueStatement)
            return ((BooleanValueStatement) formula).getValue();
        throw new ExecutionException("Not logical value");
    }
}
