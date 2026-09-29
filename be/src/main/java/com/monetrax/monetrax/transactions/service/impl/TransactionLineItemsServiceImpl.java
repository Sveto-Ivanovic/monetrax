package com.monetrax.monetrax.transactions.service.impl;

import com.monetrax.monetrax.transactions.dto.TransactionCreateUpdateResponse;
import com.monetrax.monetrax.transactions.dto.TransactionLineItemsCreate;
import com.monetrax.monetrax.transactions.entity.TransactionEntity;
import com.monetrax.monetrax.transactions.entity.TransactionLineItemsEntity;
import com.monetrax.monetrax.transactions.exceptions.MissingTransactionLikeEntityException;
import com.monetrax.monetrax.transactions.repository.TransactionLineItemsRepository;
import com.monetrax.monetrax.transactions.repository.TransactionRepository;
import com.monetrax.monetrax.transactions.service.TransactionLineItemsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
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
        log.info("Creating transaction line item [transactionId={}, userId={}]", transactionId, userId);

        TransactionEntity transactionEntity = transactionRepository.fetchUserTransaction(transactionId, userId).orElseThrow(() -> {
            log.warn("Line item creation failed, transaction not found [transactionId={}, userId={}]", transactionId, userId);
            return new MissingTransactionLikeEntityException("No such transaction found !");
        });
        var item = transactionLineItemsRepository.save(
                TransactionLineItemsEntity.builder()
                        .transaction(transactionEntity)
                        .amount(transactionLineItemsCreate.getAmount())
                        .productName(transactionLineItemsCreate.getProductName())
                        .build()
        );
        log.info("Transaction line item created [lineItemId={}, transactionId={}, userId={}]", item.getLineItemId(), transactionId, userId);
        return new TransactionCreateUpdateResponse("Successfully created transaction line product item.", item.getLineItemId());
    }

    @Override
    public TransactionCreateUpdateResponse deleteTransactionLineItem(UUID userId, UUID transactionLineId, UUID transactionId) {
        log.info("Deleting transaction line item [lineItemId={}, transactionId={}, userId={}]", transactionLineId, transactionId, userId);

        TransactionEntity transactionEntity = transactionRepository.fetchUserTransaction(transactionId, userId).orElseThrow(() -> {
            log.warn("Line item deletion failed, transaction not found [transactionId={}, userId={}]", transactionId, userId);
            return new MissingTransactionLikeEntityException("No such transaction found !");
        });
        TransactionLineItemsEntity transactionLineItemEntity = transactionLineItemsRepository.fetchByIds(transactionId, transactionLineId).orElseThrow(() -> {
            log.warn("Line item deletion failed, item not found [lineItemId={}, transactionId={}, userId={}]", transactionLineId, transactionId, userId);
            return new MissingTransactionLikeEntityException("No such  transaction line product item found !");
        });
        transactionLineItemsRepository.delete(transactionLineItemEntity);
        log.info("Transaction line item deleted [lineItemId={}, transactionId={}, userId={}]", transactionLineId, transactionId, userId);
        return new TransactionCreateUpdateResponse("Successfully deleted transaction line product info item.", transactionLineItemEntity.getLineItemId());
    }
}