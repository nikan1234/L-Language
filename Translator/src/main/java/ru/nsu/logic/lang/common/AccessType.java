package ru.nsu.logic.lang.common;

import java.util.EnumSet;

public enum AccessType {

    PUBLIC,
    PROTECTED,
    PRIVATE;

    public static class Masks {
        public static final EnumSet<AccessType> ALL = EnumSet.allOf(AccessType.class);
        public static final EnumSet<AccessType> ONLY_PUBLIC = EnumSet.of(PUBLIC);
        public static final EnumSet<AccessType> PUBLIC_AND_PROTECTED = EnumSet.of(PUBLIC, PROTECTED);
    }


    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
