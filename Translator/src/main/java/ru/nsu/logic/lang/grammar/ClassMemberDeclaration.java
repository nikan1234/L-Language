package ru.nsu.logic.lang.grammar;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.logic.lang.grammar.common.IDeclaration;
import ru.nsu.logic.lang.grammar.common.AccessTypeEnum;

public class ClassMemberDeclaration extends SimpleNode implements IDeclaration {

    @Getter
    @Setter
    private AccessTypeEnum accessType;

    @Getter
    @Setter
    private String name;

    public ClassMemberDeclaration(int i) {
        super(i);
    }
}
