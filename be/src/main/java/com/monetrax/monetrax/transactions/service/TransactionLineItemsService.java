package com.monetrax.monetrax.transactions.service;

import com.monetrax.monetrax.transactions.dto.TransactionCreateUpdateResponse;
import com.monetrax.monetrax.transactions.dto.TransactionLineItemsCreate;

import java.util.UUID;

public interface TransactionLineItemsService {
    public TransactionCreateUpdateResponse createTransactionLineItem(TransactionLineItemsCreate transactionLineItemsCreate, UUID userId, UUID transactionId);
    public TransactionCreateUpdateResponse deleteTransactionLineItem(UUID userId, UUID transactionLineId, UUID transactionId);
}
