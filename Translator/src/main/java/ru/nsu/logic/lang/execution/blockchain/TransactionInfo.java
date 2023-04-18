package ru.nsu.logic.lang.execution.blockchain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.nsu.logic.lang.compilation.common.IStatement;
import ru.nsu.logic.lang.execution.blockchain.common.ITransactionInfo;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class TransactionInfo implements ITransactionInfo {
    @Getter
    private final UUID identifier;
    @Getter
    private final String contractName;
    @Getter
    private final String methodName;
    @Getter
    private final Map<String, IStatement> parameters;
}
