package ru.nsu.logic.lang.execution.blockchain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.nsu.logic.lang.execution.blockchain.common.ISmartContractMethodId;

@AllArgsConstructor
@EqualsAndHashCode
public class SmartContractMethodId implements ISmartContractMethodId {
    @Getter
    private final String methodName;
}
