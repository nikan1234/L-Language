package ru.nsu.logic.lang.execution.blockchain.common;


import ru.nsu.logic.lang.compilation.common.IStatement;

import java.util.Map;
import java.util.UUID;

public interface ITransactionInfo {
    UUID getIdentifier();
    ISmartContractId getSmartContractId();
    ISmartContractMethodId getSmartContractMethodId();
    Map<String, IStatement> getParameters();
}
