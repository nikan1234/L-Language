package ru.nsu.logic.lang.compilation.common;

import ru.nsu.logic.lang.common.AccessType;
import ru.nsu.logic.lang.execution.common.ExecutionException;

import java.util.EnumSet;


public interface IObject extends IStatement {

    interface IMemberStorage {
        IStatement lookup(final String memberName);
        void store(final String memberName, final IStatement statement);
    }

    IStatement getMemberValue(final String memberName,
                              final EnumSet<AccessType> accessMask) throws ExecutionException;

    void setMemberValue(final String memberName,
                        final IStatement statement,
                        final EnumSet<AccessType> accessMask) throws ExecutionException;
}
