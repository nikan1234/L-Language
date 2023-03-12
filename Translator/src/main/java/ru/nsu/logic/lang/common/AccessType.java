package ru.nsu.logic.lang.common;

public enum AccessType {
    PUBLIC,
    PROTECTED,
    PRIVATE;


    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
