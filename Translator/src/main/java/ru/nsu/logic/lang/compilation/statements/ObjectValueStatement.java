package ru.nsu.logic.lang.compilation.statements;

import lombok.Getter;
import lombok.With;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.common.AccessType;
import ru.nsu.logic.lang.compilation.common.ICompiledClass;
import ru.nsu.logic.lang.compilation.common.IMember;
import ru.nsu.logic.lang.compilation.common.IObject;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class ObjectValueStatement implements IObject {

    private final ICompiledClass myClass;
    private final IMemberStorage storage;

    @With
    @Getter
    private final FileLocation location;


    private static class LocalMemberStorage implements IMemberStorage {
        private final Map<String, IStatement> statementMap = new HashMap<>();

        LocalMemberStorage(final ICompiledClass compiledClass, final FileLocation location) {
            compiledClass.getMembers().forEach(m -> this.statementMap.put(m.getName(), new NullValueStatement(location)));
        }

        @Override
        public IStatement lookup(final String memberName) {
            return statementMap.get(memberName);
        }

        @Override
        public void store(final String memberName, final IStatement statement) {
            statementMap.put(memberName, statement);
        }
    }

    /// Initializes all members with nil
    public ObjectValueStatement(final ICompiledClass myClass, final FileLocation location) {
        this.myClass = myClass;
        this.storage = new LocalMemberStorage(myClass, location);
        this.location = location;
    }

    public ObjectValueStatement(final ICompiledClass myClass,
                                final IMemberStorage externalStorage,
                                final FileLocation location) {
        this.myClass = myClass;
        this.storage = externalStorage;
        this.location = location;
    }

    public ICompiledClass myClass() {
        return myClass;
    }

    @Override
    public ExecutionResult<IStatement> execute(IVirtualMachine machine) throws ExecutionException {
        return completed(this);
    }

    @Override
    public IStatement getMemberValue(final String memberName,
                                     final EnumSet<AccessType> accessMask) throws ExecutionException {
        validateAccess(memberName, accessMask);
        return storage.lookup(memberName);
    }

    @Override
    public void setMemberValue(final String memberName,
                               final IStatement statement,
                               final EnumSet<AccessType> accessMask) throws ExecutionException {
        validateAccess(memberName, accessMask);
        storage.store(memberName, statement);
    }

    private void validateAccess(final String memberName,
                                final EnumSet<AccessType> accessMask) throws ExecutionException {
        final IMember member = myClass.getMembers().stream()
                .filter(m -> m.getName().equals(memberName)).findAny().orElse(null);
        if (member == null)
            throw new ExecutionException("Member" + memberName + " not found");

        if (!accessMask.contains(member.getAccessType()))
            throw new ExecutionException("Cannot access " + memberName + " which declared " + member.getAccessType());

    }
}
