package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.logic.lang.grammar.common.IDeclaration;

public class FunctionDeclaration extends SimpleNode implements IDeclaration {
    @Getter
    @Setter
    private String name;

    public FunctionDeclaration(int i) {
        super(i);
    }
}
