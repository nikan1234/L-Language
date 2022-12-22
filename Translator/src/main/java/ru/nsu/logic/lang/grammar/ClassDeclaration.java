package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.logic.lang.base.grammar.IDeclaration;

public class ClassDeclaration extends SimpleNode implements IDeclaration {

    @Getter
    @Setter
    String className;

    @Getter
    @Setter
    String baseClass;

    public ClassDeclaration(int i) {
        super(i);
    }

    public ClassDeclaration(LStatement p, int i) {
        super(p, i);
    }
}
