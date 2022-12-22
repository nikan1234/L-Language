package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.logic.lang.base.grammar.IDeclaration;

public class ClassMethodDeclaration extends SimpleNode implements IDeclaration {

    @Getter
    @Setter
    private AccessTypeEnum accessType;

    @Setter
    @Getter
    private String name;

    public ClassMethodDeclaration(int i) {
        super(i);
    }

    public ClassMethodDeclaration(LStatement p, int i) {
        super(p, i);
    }
}
