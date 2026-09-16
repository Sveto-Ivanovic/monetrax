package com.monetrax.monetrax.transactions.service;

import com.monetrax.monetrax.transactions.dto.*;

import java.util.UUID;

public interface TransactionService {
    TransactionInformation getTransactionInformation(UUID transactionId, UUID userId);
    ListOfAccountTransactions getAccountTransactions(UUID accountId, UUID userId);
    TransactionCreateUpdateResponse createTransaction(TransactionCreate transactionCreate, UUID userId, UUID accountId);
    TransactionCreateUpdateResponse updateTransaction(TransactionUpdate transactionUpdate, UUID userId, UUID transactionId);
    TransactionCreateUpdateResponse deleteTransaction(UUID userId, UUID transactionId);
}
