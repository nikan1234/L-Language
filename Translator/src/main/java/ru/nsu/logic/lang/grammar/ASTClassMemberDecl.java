package ru.nsu.logic.lang.grammar;


import lombok.Getter;
import lombok.Setter;

public class ASTClassMemberDecl extends SimpleNode {
    public enum AccessType {
        PUBLIC,
        PRIVATE,
        PROTECTED
    }

    @Getter
    @Setter
    private AccessType accessType;

    public ASTClassMemberDecl(int i) {
        super(i);
    }

    public ASTClassMemberDecl(LStatement p, int i) {
        super(p, i);
    }

    @Override
    public String toString() {
        return accessType.name().toLowerCase() + ' ' + jjtGetValue();
    }
}
