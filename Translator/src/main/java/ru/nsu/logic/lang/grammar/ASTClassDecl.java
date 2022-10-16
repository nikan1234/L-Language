package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;

public class ASTClassDecl extends SimpleNode {
    @Getter
    @Setter
    String className;

    public ASTClassDecl(int i) {
        super(i);
    }

    public ASTClassDecl(LStatement p, int i) {
        super(p, i);
    }

    @Override
    public String toString() {
        return "Class " + className;
    }
}
