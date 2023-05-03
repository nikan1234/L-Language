package ru.nsu.logic.lang.compilation.statements;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.common.AccessType;
import ru.nsu.logic.lang.compilation.common.*;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

import java.util.*;

@AllArgsConstructor
public class ObjectValueStatement implements IObject {

    @With
    private final ICompiledClass myClass;
    private final IObjectMemberStorage memberStorage;

    @With
    @Getter
    private final FileLocation location;


    private static class LocalMemberStorage implements IObjectMemberStorage {
        private final Map<IMember, IStatement> statementMap = new HashMap<>();

        LocalMemberStorage(final ICompiledClass compiledClass) {
            compiledClass.enumerateMembers().forEach(member -> this.statementMap.put(
                    member, new NullValueStatement(member.getLocation())));
        }

        @Override
        public IStatement lookup(final IMember member) {
            return statementMap.get(member);
        }

        @Override
        public void store(final IMember member, final IStatement value) {
            statementMap.put(member, value);
        }
    }


    /// Initializes all members with nil
    public ObjectValueStatement(final ICompiledClass myClass,
                                final FileLocation location) {
        this(myClass, new LocalMemberStorage(myClass), location);
    }

    @Override
    public ExecutionResult<IStatement> execute(IVirtualMachine machine) throws ExecutionException {
        return completed(this);
    }

    @Override
    public IObject toBase() throws ExecutionException {
        final Optional<ICompiledClass> base = myClass.getBase();
        if (!base.isPresent())
            throw new ExecutionException("No base class found");

        return withMyClass(base.get());
    }

    @Override
    public IObject toBase(final ICompiledClass baseClass) throws ExecutionException {
        if (myClass == baseClass)
            return this;

        ICompiledClass currentClass = myClass;
        while (currentClass != null) {
            if (currentClass == baseClass) {
                return withMyClass(currentClass);
            }
            currentClass = currentClass.getBase().orElse(null);
        }
        throw new ExecutionException("Cannot cast to " + baseClass.getName());
    }

    @Override
    public ICompiledClass getObjectClass() {
        return myClass;
    }

    @Override
    public IStatement getMemberValue(final String memberName,
                                     final EnumSet<AccessType> accessMask) throws ExecutionException {
        final IMember member = myClass.accessMember(memberName, accessMask);
        return memberStorage.lookup(member);
    }

    @Override
    public void setMemberValue(final String memberName,
                               final IStatement statement,
                               final EnumSet<AccessType> accessMask) throws ExecutionException {
        final IMember member = myClass.accessMember(memberName, accessMask);
        memberStorage.store(member, statement);
    }
}
