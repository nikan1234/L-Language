package ru.nsu.logic.lang.compilation.compiler;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import ru.nsu.logic.lang.common.AccessTypeEnum;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.compilation.common.ICompiledFunction;
import ru.nsu.logic.lang.compilation.common.IStatement;

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
    @Singular("statement")
    final private List<IStatement> body;

    @Getter
    final private FileLocation location;
}
