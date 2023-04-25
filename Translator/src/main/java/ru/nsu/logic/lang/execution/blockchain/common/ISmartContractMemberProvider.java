package ru.nsu.logic.lang.execution.blockchain.common;

import java.util.Optional;

public interface ISmartContractMemberProvider {
    Optional<ISmartContractMember> findMember(final ITransactionInfo transactionInfo,
                                              final ISmartContractMemberId memberId);
}
