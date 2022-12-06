package ru.nsu.logic.lang.compilator;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class CompilationRegistry<T extends CompilationRegistry.IEntry> {

    final private List<T> registry = new LinkedList<>();

    public interface IEntry {
        String getName();
    }

    public void add(final T entry) {
        registry.add(entry);
    }

    public Optional<T> lookup(final String name) {
        return registry.stream().filter(e -> e.getName().equals(name)).findFirst();
    }

    public boolean isBefore(final String first, final String second) {
        // TODO implement
        return true;
    }
}
