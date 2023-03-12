package ru.nsu.logic.lang.compilation.compiler;

import lombok.Builder;
import lombok.Getter;
import ru.nsu.logic.lang.compilation.common.ICompiledClass;
import ru.nsu.logic.lang.compilation.common.ICompiledFunction;
import ru.nsu.logic.lang.ast.FileLocation;


@Builder
public class CompiledClass  implements ICompiledClass {
    @Getter
    final private String name;
    @Getter
    final private FileLocation location;

    @Override
    public ICompiledFunction getMethod(String name) {
        return null;
    }
}