package ru.nsu.logic.lang.compilation;

import lombok.Builder;
import lombok.Getter;
import ru.nsu.logic.lang.compilation.common.ICompiledClass;
import ru.nsu.logic.lang.compilation.common.ICompiledFunction;


@Builder
public class CompiledClass  implements ICompiledClass {
    @Getter
    final private String name;

    @Override
    public ICompiledFunction getMethod(String name) {
        return null;
    }
}
