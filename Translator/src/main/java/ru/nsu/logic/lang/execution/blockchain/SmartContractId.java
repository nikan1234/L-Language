package ru.nsu.logic.lang.execution.blockchain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.nsu.logic.lang.execution.blockchain.common.ISmartContractId;

@AllArgsConstructor
@EqualsAndHashCode
public class SmartContractId implements ISmartContractId {
    @Getter
    private final String contractName;
}
