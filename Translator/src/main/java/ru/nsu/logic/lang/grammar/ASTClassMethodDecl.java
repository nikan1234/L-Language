package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;

public class ASTClassMethodDecl extends SimpleNode {

    @Getter
    @Setter
    private AccessTypeEnum accessType;

    @Setter
    @Getter
    private String name;

    public ASTClassMethodDecl(int i) {
        super(i);
    }

    public ASTClassMethodDecl(LStatement p, int i) {
        super(p, i);
    }

    @Override
    public String toString() {
        return accessType + " " + name + "()";
    }
}
