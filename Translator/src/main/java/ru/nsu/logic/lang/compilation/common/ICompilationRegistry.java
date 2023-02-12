package ru.nsu.logic.lang.compilation.common;


import ru.nsu.logic.lang.grammar.common.FileLocation;

import java.util.Optional;

public interface ICompilationRegistry<T extends ICompilationRegistry.IEntry> {
    interface IEntry {
        String getName();
        FileLocation getLocation();
    }

    void add(final T entry);
    Optional<T> lookup(final String name);
}
