package ru.nsu.logic.lang.compilation.compiler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import ru.nsu.logic.lang.common.AccessType;
import ru.nsu.logic.lang.compilation.common.*;
import ru.nsu.logic.lang.ast.FileLocation;

import java.util.List;
import java.util.Optional;


@Builder
public class CompiledClass  implements ICompiledClass {

    @AllArgsConstructor
    static public class Member implements IMember {
        @Getter
        final private String name;
        @Getter
        final private AccessType accessType;
        @Getter
        final private FileLocation location;
    }

    @Builder
    static public class Method implements ICompiledMethod {
        @Getter
        final private String name;
        @Getter
        final private AccessType accessType;
        @Getter
        @Singular("arg")
        final private List<String> arguments;
        @Getter
        @Singular("statement")
        final private List<IStatement> body;
        @Getter
        final private FileLocation location;
    }

    @Getter
    final private String name;
    @Getter
    final private FileLocation location;
    @Getter
    @Singular("member")
    final private List<IMember> members;
    final private ICompiledMethod constructor; // shortcut to ctor stored in 'methods'
    final private ICompilationRegistry<ICompiledMethod> methods;

    @Override
    public Optional<ICompiledMethod> getConstructor() {
        return constructor != null ? Optional.of(constructor) : Optional.empty();
    }

    @Override
    public Optional<ICompiledMethod> getMethod(String name) {
        return methods.lookup(name);
    }
}
