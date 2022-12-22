package ru.nsu.logic.lang.grammar;

import lombok.Setter;
import lombok.Getter;
import ru.nsu.logic.lang.base.grammar.IDeclaration;

public class FunctionDeclaration extends SimpleNode implements IDeclaration {
    @Getter
    @Setter
    private String name;

    public FunctionDeclaration(int i) {
        super(i);
    }

    public FunctionDeclaration(LStatement p, int i) {
        super(p, i);
    }
}
