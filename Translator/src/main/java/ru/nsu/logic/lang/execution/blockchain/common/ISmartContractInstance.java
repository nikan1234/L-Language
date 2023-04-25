package ru.nsu.logic.lang.execution.blockchain.common;

import ru.nsu.logic.lang.compilation.common.ICompiledMethod;
import ru.nsu.logic.lang.compilation.common.ICompiledProgram;
import ru.nsu.logic.lang.compilation.common.IObject;

public interface ISmartContractInstance {
    ICompiledMethod getContractMethod();
    ICompiledProgram getContractProgram();

    IObject getInstance(final ITransaction transaction);
}
