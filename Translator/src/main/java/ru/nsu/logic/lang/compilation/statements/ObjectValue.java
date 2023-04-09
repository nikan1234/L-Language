package ru.nsu.logic.lang.compilation.statements;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import ru.nsu.logic.lang.ast.FileLocation;
import ru.nsu.logic.lang.common.AccessType;
import ru.nsu.logic.lang.compilation.common.ICompiledClass;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.common.ExecutionException;
import ru.nsu.logic.lang.execution.common.IVirtualMachine;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class ObjectValue implements IStatement {

    @AllArgsConstructor
    private static class Member {
        private final AccessType accessType;
        private IStatement value;
    }

    @Getter
    private final String className;
    @Getter
    private final Map<String, Member> members;
    @With
    @Getter
    private final FileLocation location;

    /// Initializes all members with nil
    public ObjectValue(final ICompiledClass myClass, final FileLocation location) {
        this.className = myClass.getName();
        this.location = location;
        this.members = new HashMap<>();

        myClass.getMembers().forEach(m ->
                this.members.put(m.getName(), new Member(m.getAccessType(), new NullValue(location))));
    }

    public IStatement getMemberValue(final String memberName,
                                     final EnumSet<AccessType> accessMask) throws ExecutionException {
        return accessMember(memberName, accessMask).value;
    }

    public void setMemberValue(final String memberName,
                               final IStatement statement,
                               final EnumSet<AccessType> accessMask) throws ExecutionException {
        accessMember(memberName, accessMask).value = statement;
    }

    @Override
    public ExecutionResult<IStatement> execute(IVirtualMachine machine) throws ExecutionException {
        return new ExecutionResult<>(this, true);
    }

    private Member accessMember(final String memberName,
                                final EnumSet<AccessType> accessMask) throws ExecutionException {
        final Member member = this.members.get(memberName);
        if (member == null)
            throw new ExecutionException("Member" + memberName + " not found");

        if (!accessMask.contains(member.accessType))
            throw new ExecutionException("Cannot access " + memberName + " which declared " + member.accessType);
        return member;
    }
}
