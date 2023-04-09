package ru.nsu.logic.lang.compilation.common;

import ru.nsu.logic.lang.common.AccessType;

public interface IMember {
    AccessType getAccessType();
    String getName();
}
