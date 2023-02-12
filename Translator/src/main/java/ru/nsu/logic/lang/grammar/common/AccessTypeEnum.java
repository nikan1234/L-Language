package ru.nsu.logic.lang.grammar.common;

public enum AccessTypeEnum {
    PUBLIC,
    PROTECTED,
    PRIVATE;


    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
