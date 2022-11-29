package ru.nsu.logic.lang.grammar;

import lombok.Setter;
import lombok.Getter;

public class ASTFunctionDecl extends SimpleNode {
    @Getter
    @Setter
    private String name;

    public ASTFunctionDecl(int i) {
        super(i);
    }

    public ASTFunctionDecl(LStatement p, int i) {
        super(p, i);
    }
}
