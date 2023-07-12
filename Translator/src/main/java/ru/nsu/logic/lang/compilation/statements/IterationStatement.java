package ru.nsu.logic.lang.compilation.statements;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.compilation.statements.logic.BooleanValueStatement;
import ru.nsu.logic.lang.compilation.statements.logic.IFormula;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class IterationStatement implements IStatement {

    @AllArgsConstructor
    private static class State {
        private int iteration;
        private IFormula formula;
        private IStatement aggregate;
    }

    @With(AccessLevel.PRIVATE)
    private State state;

    @With
    @Getter
    private final FileLocation location;

    @With(AccessLevel.PRIVATE)
    private final IStatement maxIterations;

    @With(AccessLevel.PRIVATE)
    private final IStatement init;

    @With(AccessLevel.PRIVATE)
    private final VariableStatement iterVariable;

    private final IStatement aggregatePrototype;
    private final IFormula checkFormulaPrototype;


    public IterationStatement(final AssignmentStatement init,
                              final IStatement maxIterations,
                              final IStatement aggregate,
                              final IFormula checkFormula,
                              final FileLocation location) {
        this(new State(0, null, null),
             location, maxIterations, init,
             (VariableStatement) init.getTarget(),
             aggregate, checkFormula);
    }

    @Override
    public ExecutionResult<IStatement> execute(final IVirtualMachine machine) throws ExecutionException {
        final ExecutionResult<IStatement> maxIterationsExec = this.maxIterations.execute(machine);
        if (!maxIterationsExec.isCompleted())
            return uncompleted(withMaxIterations(maxIterationsExec.getValue()));

        IStatement init = this.init;
        if (null != init) {
            final ExecutionResult<IStatement> initExec = this.init.execute(machine);
            if (!initExec.isCompleted())
                return uncompleted(withInit(initExec.getValue()));

            init = initExec.getValue();
        }

        final State state = new State(this.state.iteration, this.state.formula, this.state.aggregate);
        while (state.iteration < ((NumberValueStatement)maxIterationsExec.getValue()).asInt()) {
            /* Evaluate formula on current iter */
            if (null == state.formula)
                state.formula = this.checkFormulaPrototype;

            final ExecutionResult<IFormula> formulaExec = state.formula.execute(machine);
            state.formula = formulaExec.getValue();
            if (!formulaExec.isCompleted())
                return uncompleted(withMaxIterations(maxIterationsExec.getValue()).withInit(init).withState(state));

            if (!((BooleanValueStatement)state.formula).getValue())
                return iterVariable.execute(machine); // completed

            /* Evaluate aggregate on current iter */
            if (null == state.aggregate)
                state.aggregate = this.aggregatePrototype;

            final ExecutionResult<IStatement> aggregateExec = state.aggregate.execute(machine);
            state.aggregate = aggregateExec.getValue();
            if (!aggregateExec.isCompleted())
                return uncompleted(withMaxIterations(maxIterationsExec.getValue()).withInit(init).withState(state));

            iterVariable.setValue(machine, aggregateExec.getValue());

            ++state.iteration;
            state.formula = null;
            state.aggregate = null;
        }
        return completed(new NullValueStatement(location));
    }
}
