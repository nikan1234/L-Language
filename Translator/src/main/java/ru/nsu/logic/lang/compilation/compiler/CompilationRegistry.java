package ru.nsu.logic.lang.compilation.compiler;

import ru.nsu.logic.lang.compilation.common.CompilationException;
import ru.nsu.logic.lang.compilation.common.ICompilationRegistry;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class CompilationRegistry<T extends ICompilationRegistry.IEntry> implements ICompilationRegistry<T> {

    final private List<T> registry = new LinkedList<>();

    @Override
    public void add(final T entry) throws CompilationException {
        if (lookup(entry.getName()).isPresent())
            throw new CompilationException("Function " + entry.getName() + " previously declared");
        registry.add(entry);
    }

    @Override
    public Optional<T> lookup(final String name) {
        return registry.stream().filter(e -> e.getName().equals(name)).findFirst();
    }
}
