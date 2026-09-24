package com.monetrax.monetrax.transactions.service;

import com.monetrax.monetrax.transactions.dto.TransactionCreateUpdateResponse;
import com.monetrax.monetrax.transactions.dto.TransactionRecurrenceInformation;
import com.monetrax.monetrax.transactions.dto.TransactionRecurrenceResponse;

import java.util.UUID;

public interface TransactionRecurrenceRuleService {
    public TransactionRecurrenceResponse getAllTransactionRulesForAccount(UUID accountId, UUID userId);
    public TransactionCreateUpdateResponse deleteTransactionRule(UUID transactionRuleId,  UUID transactionId, UUID userId);
}
