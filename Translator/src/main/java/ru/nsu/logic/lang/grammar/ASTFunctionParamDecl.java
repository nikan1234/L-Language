package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;

public class ASTFunctionParamDecl extends SimpleNode {
    @Getter
    @Setter
    String paramName;

    public ASTFunctionParamDecl(int i) {
        super(i);
    }

    public ASTFunctionParamDecl(LStatement p, int i) {
        super(p, i);
    }

    @Override
    public String toString() {
        return paramName;
    }
}
