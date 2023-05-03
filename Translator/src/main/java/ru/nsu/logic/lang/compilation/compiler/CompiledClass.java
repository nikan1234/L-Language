package ru.nsu.logic.lang.compilation.compiler;

import lombok.*;
import ru.nsu.logic.lang.common.AccessType;
import ru.nsu.logic.lang.compilation.common.*;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.execution.common.ExecutionException;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;


public class CompiledClass  implements ICompiledClass {

    @AllArgsConstructor
    static public class Member implements IMember {
        @Getter
        final private ICompiledClass owner;
        @Getter
        final private String name;
        @Getter
        final private AccessType accessType;
        @Getter
        final private FileLocation location;
    }

    @Builder
    @AllArgsConstructor
    static public class Method implements ICompiledMethod {
        @Getter
        final private ICompiledClass owner;
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
    final private ICompiledClass base;

    @Setter
    private List<IMember> members;
    @Setter
    private ICompiledMethod constructor; // shortcut to ctor stored in 'methods'
    @Setter
    private ICompilationRegistry<ICompiledMethod> methods;

    public CompiledClass(final String name, final FileLocation location, final ICompiledClass base) {
        this.name = name;
        this.location = location;
        this.base = base;
    }

    @Override
    public Optional<ICompiledClass> getBase() {
        return base != null ? Optional.of(base) : Optional.empty();
    }

    @Override
    public List<IMember> enumerateMembers() {
        return members;
    }

    @Override
    public IMember accessMember(final String name, final EnumSet<AccessType> access) throws ExecutionException {
        final Optional<IMember> member = members.stream().filter(m -> m.getName().equals(name)).findFirst();

        if (!member.isPresent()) { // try to found in base class
            if (base == null)
                throw new ExecutionException("Member '" + name + "' not found");

            return base.accessMember(name, AccessType.Masks.merge(access, AccessType.Masks.PUBLIC_AND_PROTECTED));
        }

        if (!access.contains(member.get().getAccessType()))
            throw new ExecutionException("Cannot access " + name + " which declared " + member.get().getAccessType());
        return member.get();
    }

    @Override
    public Optional<ICompiledMethod> getConstructor(final EnumSet<AccessType> access) throws ExecutionException
    {
        if (null == constructor)
            return Optional.empty();

        if (!access.contains(constructor.getAccessType()))
            throw new ExecutionException("Cannot access " + name + " constructor which declared " +
                                         constructor.getAccessType());

        return Optional.of(constructor);
    }

    @Override
    public ICompiledMethod accessMethod(final String name, final EnumSet<AccessType> access) throws ExecutionException {
        final Optional<ICompiledMethod> method = methods.lookup(name);
        if (!method.isPresent()) { // try to found in base class
            if (base == null)
                throw new ExecutionException("method" + name + " not found");

            return base.accessMethod(name, AccessType.Masks.merge(access, AccessType.Masks.PUBLIC_AND_PROTECTED));
        }

        if (!access.contains(method.get().getAccessType()))
            throw new ExecutionException("Cannot access " + name + " which declared " + method.get().getAccessType());
        return method.get();
    }
}
