package ru.nsu.logic.lang.compilator;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import ru.nsu.logic.lang.base.compilation.ICompiledFunction;
import ru.nsu.logic.lang.base.grammar.IStatement;
import ru.nsu.logic.lang.grammar.AccessTypeEnum;

import java.util.List;

@Builder
public class CompiledFunction implements ICompiledFunction {
    @Getter
    final private String name;

    @Getter
    final private AccessTypeEnum accessType;

    @Getter
    @Singular("arg")
    final private List<String> arguments;

    @Getter
    @Singular("statment")
    final private List<IStatement> body;
}
