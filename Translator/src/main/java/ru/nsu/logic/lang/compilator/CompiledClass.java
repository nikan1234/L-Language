package ru.nsu.logic.lang.compilator;

import lombok.Builder;
import lombok.Getter;


@Builder
public class CompiledClass  implements CompilationRegistry.IEntry {
    @Getter
    final private String name;

}
