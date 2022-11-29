package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;

public class ASTClassMemberDecl extends SimpleNode {

    @Getter
    @Setter
    private AccessTypeEnum accessType;

    @Getter
    @Setter
    private String name;


    public ASTClassMemberDecl(int i) {
        super(i);
    }

    public ASTClassMemberDecl(LStatement p, int i) {
        super(p, i);
    }

    @Override
    public String toString() {
        return accessType + " " + name;
    }
}