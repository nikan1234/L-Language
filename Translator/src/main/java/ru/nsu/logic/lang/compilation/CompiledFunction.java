package ru.nsu.logic.lang.compilation;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import ru.nsu.logic.lang.compilation.common.ICompiledFunction;
import ru.nsu.logic.lang.grammar.common.IStatement;
import ru.nsu.logic.lang.grammar.common.AccessTypeEnum;

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
