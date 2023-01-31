package ru.nsu.logic.lang.compilator;

import lombok.Builder;
import lombok.Getter;
import ru.nsu.logic.lang.base.compilation.ICompiledClass;
import ru.nsu.logic.lang.base.compilation.ICompiledFunction;


@Builder
public class CompiledClass  implements ICompiledClass {
    @Getter
    final private String name;

    @Override
    public ICompiledFunction getMethod(String name) {
        return null;
    }
}
