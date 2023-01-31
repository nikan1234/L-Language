package ru.nsu.logic.lang.grammar;

public enum AccessTypeEnum {
    PUBLIC,
    PROTECTED,
    PRIVATE;


    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
