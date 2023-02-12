package ru.nsu.logic.lang.compilation.common;


import java.util.Optional;

public interface ICompilationRegistry<T extends ICompilationRegistry.IEntry> {
    interface IEntry {
        String getName();
    }

    void add(final T entry);
    Optional<T> lookup(final String name);
}
