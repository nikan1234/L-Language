package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;

public class ASTParamVariableDecl extends SimpleNode {
    @Getter
    @Setter
    String paramName;

    public ASTParamVariableDecl(int i) {
        super(i);
    }

    public ASTParamVariableDecl(LStatement p, int i) {
        super(p, i);
    }

    @Override
    public String toString() {
        return paramName;
    }
}
