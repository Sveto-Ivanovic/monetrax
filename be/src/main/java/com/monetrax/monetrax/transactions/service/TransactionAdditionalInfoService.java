package com.monetrax.monetrax.transactions.service;

import com.monetrax.monetrax.transactions.dto.TransactionAdditionalInfoCreate;
import com.monetrax.monetrax.transactions.dto.TransactionCreateUpdateResponse;
import com.monetrax.monetrax.transactions.dto.TransactionLineItemsCreate;

import java.util.UUID;

public interface TransactionAdditionalInfoService {
    public TransactionCreateUpdateResponse createTransactionAdditionalInfoItem(TransactionAdditionalInfoCreate transactionAdditionalInfoCreate, UUID userId, UUID transactionId);
    public TransactionCreateUpdateResponse deleteTransactionAdditionalInfoItem(UUID userId, UUID transactionAdditionalInfoId, UUID transactionId);
}
