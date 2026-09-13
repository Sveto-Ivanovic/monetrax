package com.monetrax.monetrax.transactions.service;

import com.monetrax.monetrax.transactions.dto.ListOfAccountTransactions;
import com.monetrax.monetrax.transactions.dto.TransactionCreate;
import com.monetrax.monetrax.transactions.dto.TransactionInformation;

import java.util.UUID;

public interface TransactionService {
    TransactionInformation getTransactionInformation(UUID transactionId, UUID userId);
    ListOfAccountTransactions getAccountTransactions(UUID accountId, UUID userId);
    TransactionInformation createTransaction(TransactionCreate transactionCreate, UUID userId, UUID accountId)
}
