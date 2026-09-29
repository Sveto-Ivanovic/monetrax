package com.monetrax.monetrax.transactions.service.impl;

import com.monetrax.monetrax.transactions.dto.TransactionAdditionalInfoCreate;
import com.monetrax.monetrax.transactions.dto.TransactionCreateUpdateResponse;
import com.monetrax.monetrax.transactions.entity.TransactionAdditionalInfoEntity;
import com.monetrax.monetrax.transactions.entity.TransactionEntity;
import com.monetrax.monetrax.transactions.exceptions.MissingTransactionLikeEntityException;
import com.monetrax.monetrax.transactions.repository.TransactionAdditionalInfoRepository;
import com.monetrax.monetrax.transactions.repository.TransactionRepository;
import com.monetrax.monetrax.transactions.service.TransactionAdditionalInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class TransactionAdditionalInfoServiceImpl implements TransactionAdditionalInfoService {

    private final TransactionAdditionalInfoRepository transactionAdditionalInfoRepository;
    private final TransactionRepository transactionRepository;

    public TransactionAdditionalInfoServiceImpl(TransactionAdditionalInfoRepository transactionAdditionalInfoRepository, TransactionRepository transactionRepository) {
        this.transactionAdditionalInfoRepository = transactionAdditionalInfoRepository;
        this.transactionRepository = transactionRepository;
    }


    @Override
    public TransactionCreateUpdateResponse createTransactionAdditionalInfoItem(TransactionAdditionalInfoCreate transactionAdditionalInfoCreate, UUID userId, UUID transactionId) {
        log.info("Creating transaction additional info item [transactionId={}, userId={}]", transactionId, userId);

        TransactionEntity transactionEntity = transactionRepository.fetchUserTransaction(transactionId, userId).orElseThrow(() -> {
            log.warn("Additional info creation failed, transaction not found [transactionId={}, userId={}]", transactionId, userId);
            return new MissingTransactionLikeEntityException("No such transaction found !");
        });
        var item = transactionAdditionalInfoRepository.save(
                TransactionAdditionalInfoEntity.builder()
                        .amount(transactionAdditionalInfoCreate.getAmount())
                        .kind(transactionAdditionalInfoCreate.getKind())
                        .label(transactionAdditionalInfoCreate.getLabel())
                        .transaction(transactionEntity)
                        .build()
        );
        log.info("Transaction additional info item created [transactionInfoId={}, transactionId={}, userId={}]", item.getTransactionInfoId(), transactionId, userId);
        return new TransactionCreateUpdateResponse("Successfully create transaction additional info item.", item.getTransactionInfoId());
    }

    @Override
    public TransactionCreateUpdateResponse deleteTransactionAdditionalInfoItem(UUID userId, UUID transactionAdditionalInfoId, UUID transactionId) {
        log.info("Deleting transaction additional info item [transactionInfoId={}, transactionId={}, userId={}]", transactionAdditionalInfoId, transactionId, userId);

        TransactionEntity transactionEntity = transactionRepository.fetchUserTransaction(transactionId, userId).orElseThrow(() -> {
            log.warn("Additional info deletion failed, transaction not found [transactionId={}, userId={}]", transactionId, userId);
            return new MissingTransactionLikeEntityException("No such transaction found !");
        });
        TransactionAdditionalInfoEntity transactionAdditionalInfoEntity = transactionAdditionalInfoRepository.fetchByIds(transactionId, transactionAdditionalInfoId).orElseThrow(() -> {
            log.warn("Additional info deletion failed, item not found [transactionInfoId={}, transactionId={}, userId={}]", transactionAdditionalInfoId, transactionId, userId);
            return new MissingTransactionLikeEntityException("No such  transaction additional info item found !");
        });
        transactionAdditionalInfoRepository.delete(transactionAdditionalInfoEntity);
        log.info("Transaction additional info item deleted [transactionInfoId={}, transactionId={}, userId={}]", transactionAdditionalInfoId, transactionId, userId);
        return new TransactionCreateUpdateResponse("Successfully deleted transaction additional info item.", transactionAdditionalInfoEntity.getTransactionInfoId());
    }

}