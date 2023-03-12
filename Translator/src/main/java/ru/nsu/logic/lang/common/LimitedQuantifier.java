package ru.nsu.logic.lang.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.With;
import ru.nsu.logic.lang.ast.Token;

@AllArgsConstructor
public class LimitedQuantifier<T> {

    public enum Quantifier {
        FORALL,
        EXISTS
    }

    public enum Selection {
        EACH_ELEMENT,
        EACH_SUBSET
    }

    @Getter
    Quantifier quantifier;

    @Getter
    String variable;

    @Getter
    Selection selection;

    @With
    @Getter
    T selectionSource;
}
