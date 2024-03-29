package ru.nsu.logic.lang.compilation.statements.logic;

import ru.nsu.logic.lang.common.LimitedQuantifier;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.compilation.statements.ListValueStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

import java.util.ArrayList;
import java.util.List;

public class QuantifierFormulaStatement implements IFormula {
    private final LimitedQuantifier<IStatement> limitedQuantifier;
    private final IFormula checkFormula;

    private final IFormula currentlyExecuted;
    private final IValuesProvider valuesProvider;


    public interface IValuesProvider {
        IValuesProvider next();
        boolean hasNext();
        IStatement value();
    }

    static public class ForeachProvider implements IValuesProvider {
        private final ListValueStatement source;
        private final int currentIndex;

        private ForeachProvider(final ListValueStatement source, final int currentIndex) {
            this.source = source;
            this.currentIndex = currentIndex;
        }

        public static ForeachProvider create(final IStatement statement) throws ExecutionException {
            assertIsList(statement);
            return new ForeachProvider((ListValueStatement)statement, -1);
        }

        @Override
        public IValuesProvider next() {
            return new ForeachProvider(source, currentIndex + 1);
        }

        @Override
        public boolean hasNext() {
            return currentIndex < source.getElements().size() - 1;
        }

        @Override
        public IStatement value() {
            return source.getElements().get(currentIndex);
        }
    }

    static public class SubseteqProvider implements IValuesProvider {
        private final ListValueStatement source;
        private final ListValueStatement currentSubseteq;

        private SubseteqProvider(final ListValueStatement source, final ListValueStatement currentSubseteq) {
            this.source = source;
            this.currentSubseteq = currentSubseteq;
        }

        public static SubseteqProvider create(final IStatement statement) throws ExecutionException {
            assertIsList(statement);
            return new SubseteqProvider(
                    (ListValueStatement)statement,
                    new ListValueStatement(new ArrayList<>(), statement.getLocation()));
        }

        @Override
        public IValuesProvider next() {
            final List<IStatement> elements = new ArrayList<>(currentSubseteq.getElements());
            elements.add(source.getElements().get(elements.size()));
            return new SubseteqProvider(source, currentSubseteq.withElements(elements));
        }

        @Override
        public boolean hasNext() {
            return currentSubseteq.getElements().size() < source.getElements().size();
        }

        @Override
        public IStatement value() {
            return currentSubseteq;
        }
    }

    public QuantifierFormulaStatement(final LimitedQuantifier<IStatement> limitedQuantifier,
                                      final IFormula checkFormula) {
        this(limitedQuantifier,checkFormula, null, null);
    }

    private QuantifierFormulaStatement(final LimitedQuantifier<IStatement> limitedQuantifier,
                                       final IFormula checkFormula,
                                       final IFormula currentlyExecuted,
                                       final IValuesProvider valuesProvider) {
        this.limitedQuantifier = limitedQuantifier;
        this.checkFormula = checkFormula;
        this.currentlyExecuted = currentlyExecuted;
        this.valuesProvider = valuesProvider;
    }

    @Override
    public ExecutionResult<IFormula> execute(final IVirtualMachine machine) throws ExecutionException {
        LimitedQuantifier<IStatement> limitedQuantifier = this.limitedQuantifier;
        IValuesProvider valuesProvider = this.valuesProvider;

        if (valuesProvider == null) {
            final IStatement selection = limitedQuantifier.getSelectionSource();
            final ExecutionResult<IStatement> result = selection.execute(machine);

            limitedQuantifier = limitedQuantifier.withSelectionSource(result.getValue());
            if (!result.isCompleted())
                return uncompleted(new QuantifierFormulaStatement(limitedQuantifier, checkFormula));

            valuesProvider = limitedQuantifier.getSelection() == LimitedQuantifier.Selection.EACH_ELEMENT ?
                    ForeachProvider.create(result.getValue()) : SubseteqProvider.create(result.getValue());
        }

        /* Here we have provider */
        IFormula currentlyExecuted = this.currentlyExecuted;
        while (valuesProvider.hasNext() || currentlyExecuted != null) {
            final ExecutionResult<IFormula> result;

            if (currentlyExecuted == null) {
                /* No uncompleted pending statements */
                valuesProvider = valuesProvider.next();
                machine.getPipeline().getCurrentEntry()
                        .initializeVariable(limitedQuantifier.getVariable(), valuesProvider.value());
                result = checkFormula.execute(machine);
            }
            else
                result = currentlyExecuted.execute(machine);

            if (!result.isCompleted()) {
                currentlyExecuted = result.getValue();
                return uncompleted(new QuantifierFormulaStatement(
                        limitedQuantifier, checkFormula, currentlyExecuted, valuesProvider));
            }

            /* Optimization */
            if (asBool(result.getValue()) && limitedQuantifier.getQuantifier() == LimitedQuantifier.Quantifier.EXISTS)
                return completed(new BooleanValueStatement(true));

            if (!asBool(result.getValue()) && limitedQuantifier.getQuantifier() == LimitedQuantifier.Quantifier.FORALL)
                return completed(new BooleanValueStatement(false));

            currentlyExecuted = null;
        }
        return completed(
                new BooleanValueStatement(limitedQuantifier.getQuantifier() == LimitedQuantifier.Quantifier.FORALL));
    }

    private static void assertIsList(final IStatement statement) throws ExecutionException {
        if (!(statement instanceof ListValueStatement))
            throw new ExecutionException("Expected list");
    }

    private static boolean asBool(final IFormula formula) throws ExecutionException {
        if (formula instanceof BooleanValueStatement)
            return ((BooleanValueStatement) formula).getValue();
        throw new ExecutionException("Expected bool");
    }
}
