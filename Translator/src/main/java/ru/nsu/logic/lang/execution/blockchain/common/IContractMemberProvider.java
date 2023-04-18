package ru.nsu.logic.lang.execution.blockchain.common;

import java.util.Optional;

public interface IContractMemberProvider {
    Optional<IContractMember> findMember(final ITransactionInfo transactionInfo,
                                         final IContractMemberId memberName);
}
