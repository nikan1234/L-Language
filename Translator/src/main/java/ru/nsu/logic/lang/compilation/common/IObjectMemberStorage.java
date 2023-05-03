package ru.nsu.logic.lang.compilation.common;

public interface IObjectMemberStorage {
    void store(final IMember member, final IStatement value);
    IStatement lookup(final IMember member);
}
