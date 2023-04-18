package ru.nsu.logic.lang.execution.blockchain.common;


import ru.nsu.logic.lang.compilation.common.IStatement;

import java.util.Map;
import java.util.UUID;

public interface ITransactionInfo {
    UUID getIdentifier();
    String getContractName();
    String getMethodName();
    Map<String, IStatement> getParameters();
}
