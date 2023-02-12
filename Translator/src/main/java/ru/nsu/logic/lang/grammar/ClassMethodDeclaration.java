package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.logic.lang.grammar.common.IDeclaration;
import ru.nsu.logic.lang.grammar.common.AccessTypeEnum;

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
}
