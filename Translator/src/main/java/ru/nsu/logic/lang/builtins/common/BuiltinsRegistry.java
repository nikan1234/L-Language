package ru.nsu.logic.lang.builtins.common;

import org.reflections.Reflections;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public enum BuiltinsRegistry {
    INSTANCE;

    private final Map<String, BuiltinBuilder<?>> builtinBuilderMap = new HashMap<>();;

    public static class BuiltinBuilder<T extends IBuiltin> {
        private final Class<T> prototype;

        private BuiltinBuilder(final Class<T> prototype) {
            this.prototype = prototype;
        }

        public IBuiltin build(final IVirtualMachine machine) {
            try {
                final IBuiltin builtin;
                builtin = prototype.getConstructor().newInstance();
                builtin.initialize(machine);
                return builtin;

            } catch (final ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private BuiltinsRegistry() {
        final Reflections reflections = new Reflections("ru.nsu.logic.lang.builtins");
        final Set<Class<?>> classes = reflections.getTypesAnnotatedWith(BuiltinClass.class);
        for (final Class<?> cls : classes) {
            final BuiltinClass annotation = cls.getAnnotation(BuiltinClass.class);
            register(annotation.name(), cls.asSubclass(IBuiltin.class));
        }
    }

    public <T extends IBuiltin> void register(final String name, final Class<T> prototype) {
        builtinBuilderMap.put(name, new BuiltinBuilder<T>(prototype));
    }

    public Optional<BuiltinBuilder<?>> lookup(final String name) {
        final BuiltinBuilder<?> builder = builtinBuilderMap.get(name);
        return builder != null ? Optional.of(builder) : Optional.empty();
    }
}
