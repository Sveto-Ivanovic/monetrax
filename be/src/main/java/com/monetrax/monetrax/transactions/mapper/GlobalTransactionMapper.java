package com.monetrax.monetrax.transactions.mapper;

import com.monetrax.monetrax.accounts.entity.AccountEntity;
import com.monetrax.monetrax.categories.dto.CategoryInformation;
import com.monetrax.monetrax.categories.entity.CategoryKind;
import com.monetrax.monetrax.transactions.dto.*;
import com.monetrax.monetrax.transactions.entity.TransactionAdditionalInfoEntity;
import com.monetrax.monetrax.transactions.entity.TransactionEntity;
import com.monetrax.monetrax.transactions.entity.TransactionLineItemsEntity;
import com.monetrax.monetrax.user.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Component
public class GlobalTransactionMapper {
    public TransactionEntity fromTransactionCreateToTransactionEntity(TransactionCreate transactionCreate,
                                                                      UserEntity userEntity,
                                                                      AccountEntity accountEntity,
                                                                      BigDecimal amountNative,
                                                                      CategoryKind categoryKind){
        return TransactionEntity.builder()
                .user(userEntity)
                .account(accountEntity)
                .amount(transactionCreate.getAmount())
                .amountNative(amountNative)
                .categoryType(categoryKind)
                .createdAt(OffsetDateTime.now())
                .currency(transactionCreate.getCurrency())
                .description(transactionCreate.getDescription())
                .name(transactionCreate.getName())
                .build();
    }

    public TransactionAdditionalInfoEntity fromTransactionAdditionalInfoCreateToTransactionAdditionalInfoEntity(TransactionAdditionalInfoCreate transactionAdditionalInfoCreate,
                                                                                                                TransactionEntity transactionEntity){
        return TransactionAdditionalInfoEntity.builder()
                .amount(transactionAdditionalInfoCreate.getAmount())
                .kind(transactionAdditionalInfoCreate.getKind())
                .label(transactionAdditionalInfoCreate.getLabel())
                .transaction(transactionEntity)
                .build();
    }

    public TransactionLineItemsEntity fromTransactionLineItemsCreateToTransactionLineItemsEntity(TransactionLineItemsCreate transactionLineItemsCreate,
                                                                                                      TransactionEntity transactionEntity){
        return TransactionLineItemsEntity.builder()
                .amount(transactionLineItemsCreate.getAmount())
                .productName(transactionLineItemsCreate.getProductName())
                .transaction(transactionEntity)
                .build();
    }


    public TransactionInformation fromTransactionEntityToTransactionInformation(TransactionEntity transactionEntity,
                                                                                List<TransactionAdditionalInfoInformation> transactionAdditionalInfoInformation,
                                                                                List<TransactionLineItemsInformation> lineItemsInformation,
                                                                                List<CategoryInformation> categories){
        return TransactionInformation.builder()
                .amount(transactionEntity.getAmount())
                .categoryType(transactionEntity.getCategoryType())
                .createdAt(transactionEntity.getCreatedAt())
                .currency(transactionEntity.getCurrency())
                .description(transactionEntity.getDescription())
                .name(transactionEntity.getDescription())
                .transactionId(transactionEntity.getTransactionId())
                .additionalInfo(transactionAdditionalInfoInformation)
                .lineItemsInformation(lineItemsInformation)
                .categories(categories)
                .build();
    }

    public TransactionAdditionalInfoInformation fromTransactionAdditionalInfoEntityToTransactionAdditionalInfoInformation(TransactionAdditionalInfoEntity transactionAdditionalInfoEntity){
        return TransactionAdditionalInfoInformation.builder()
                .amount(transactionAdditionalInfoEntity.getAmount())
                .kind(transactionAdditionalInfoEntity.getKind())
                .label(transactionAdditionalInfoEntity.getLabel())
                .transactionInfoId(transactionAdditionalInfoEntity.getTransactionInfoId())
                .build();
    }

    public TransactionLineItemsInformation fromTransactionLineItemsEntityToTransactionLineItemsInformation(TransactionLineItemsEntity transactionLineItemsEntity){
        return TransactionLineItemsInformation.builder()
                .amount(transactionLineItemsEntity.getAmount())
                .productName(transactionLineItemsEntity.getProductName())
                .lineItemId(transactionLineItemsEntity.getLineItemId())
                .build();
    }

}
