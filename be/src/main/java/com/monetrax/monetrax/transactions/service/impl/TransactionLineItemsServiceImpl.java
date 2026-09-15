package com.monetrax.monetrax.transactions.service.impl;

import com.monetrax.monetrax.transactions.dto.TransactionCreateUpdateResponse;
import com.monetrax.monetrax.transactions.dto.TransactionLineItemsCreate;
import com.monetrax.monetrax.transactions.entity.TransactionAdditionalInfoEntity;
import com.monetrax.monetrax.transactions.entity.TransactionEntity;
import com.monetrax.monetrax.transactions.entity.TransactionLineItemsEntity;
import com.monetrax.monetrax.transactions.exceptions.MissingTransactionLikeEntityException;
import com.monetrax.monetrax.transactions.repository.TransactionAdditionalInfoRepository;
import com.monetrax.monetrax.transactions.repository.TransactionLineItemsRepository;
import com.monetrax.monetrax.transactions.repository.TransactionRepository;
import com.monetrax.monetrax.transactions.service.TransactionLineItemsService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TransactionLineItemsServiceImpl implements TransactionLineItemsService {


    private final TransactionLineItemsRepository transactionLineItemsRepository;
    private final TransactionRepository transactionRepository;

    public TransactionLineItemsServiceImpl(TransactionLineItemsRepository transactionLineItemsRepository, TransactionRepository transactionRepository) {
        this.transactionLineItemsRepository = transactionLineItemsRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public TransactionCreateUpdateResponse createTransactionLineItem(TransactionLineItemsCreate transactionLineItemsCreate, UUID userId, UUID transactionId) {
        TransactionEntity transactionEntity = transactionRepository.fetchUserTransaction(transactionId, userId).orElseThrow(()-> new MissingTransactionLikeEntityException("No such transaction found !"));
        var item = transactionLineItemsRepository.save(
                TransactionLineItemsEntity.builder()
                        .transaction(transactionEntity)
                        .amount(transactionLineItemsCreate.getAmount())
                        .productName(transactionLineItemsCreate.getProductName())
                        .build()
        );
        return new TransactionCreateUpdateResponse("Successfully created transaction line product item.", item.getLineItemId());
    }

    @Override
    public TransactionCreateUpdateResponse deleteTransactionLineItem(UUID userId, UUID transactionLineId, UUID transactionId) {
        TransactionEntity transactionEntity = transactionRepository.fetchUserTransaction(transactionId, userId).orElseThrow(()-> new MissingTransactionLikeEntityException("No such transaction found !"));
        TransactionLineItemsEntity transactionAdditionalInfoEntity = transactionLineItemsRepository.fetchByIds(transactionId, transactionLineId).orElseThrow(()-> new MissingTransactionLikeEntityException("No such  transaction line product item found !"));
        transactionLineItemsRepository.delete(transactionAdditionalInfoEntity);
        return new TransactionCreateUpdateResponse("Successfully deleted transaction line product info item.", transactionAdditionalInfoEntity.getLineItemId());

    }
}
