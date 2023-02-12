package ru.nsu.logic.lang.excution.common;

import ru.nsu.logic.lang.grammar.common.FileLocation;

public interface IContext {
    String getFunctionName();
    FileLocation getLocation();
}
