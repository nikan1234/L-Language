package ru.nsu.logic.lang.execution.blockchain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.nsu.logic.lang.execution.blockchain.common.ISmartContractId;
import ru.nsu.logic.lang.execution.blockchain.common.ISmartContractMemberId;

@AllArgsConstructor
@EqualsAndHashCode
public class SmartContractMemberId implements ISmartContractMemberId {

    @Getter
    ISmartContractId smartContractId;

    @Getter
    private final String memberName;
}
