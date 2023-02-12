package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.logic.lang.grammar.common.IDeclaration;

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
}
