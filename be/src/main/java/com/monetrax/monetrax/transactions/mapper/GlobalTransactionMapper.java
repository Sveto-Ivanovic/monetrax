package com.monetrax.monetrax.transactions.mapper;

import com.monetrax.monetrax.accounts.entity.AccountEntity;
import com.monetrax.monetrax.categories.dto.CategoryInformation;
import com.monetrax.monetrax.categories.entity.CategoryEntity;
import com.monetrax.monetrax.categories.entity.CategoryKind;
import com.monetrax.monetrax.transactions.dto.*;
import com.monetrax.monetrax.transactions.entity.TransactionAdditionalInfoEntity;
import com.monetrax.monetrax.transactions.entity.TransactionEntity;
import com.monetrax.monetrax.transactions.entity.TransactionLineItemsEntity;
import com.monetrax.monetrax.transactions.entity.TransactionRecurrenceRuleEntity;
import com.monetrax.monetrax.user.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

@Component
public class GlobalTransactionMapper {
    public TransactionEntity fromTransactionCreateToTransactionEntity(TransactionCreate transactionCreate,
                                                                      UserEntity userEntity,
                                                                      AccountEntity accountEntity,
                                                                      BigDecimal amountNative,
                                                                      CategoryKind categoryKind,
                                                                      BigDecimal conversionFactor){

        OffsetDateTime creationDate = transactionCreate.getCustomCreationDate() == null
                ? OffsetDateTime.now(ZoneOffset.UTC)
                : transactionCreate.getCustomCreationDate().withOffsetSameInstant(ZoneOffset.UTC);
        return TransactionEntity.builder()
                .user(userEntity)
                .account(accountEntity)
                .conversionFactor(conversionFactor)
                .amount(transactionCreate.getAmount())
                .amountNative(amountNative)
                .categoryType(categoryKind)
                .createdAt(creationDate)
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
                .conversionFactor(transactionEntity.getConversionFactor())
                .nativeAmount(transactionEntity.getAmountNative())
                .description(transactionEntity.getDescription())
                .name(transactionEntity.getName())
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

    public TransactionInformationPart fromTransactionEntityToTransactionInformationPart(TransactionEntity transactionEntity, List<CategoryEntity> categoryEntities){
       List<String> categories = categoryEntities.stream()
               .map(CategoryEntity::getName)
               .toList();
        return TransactionInformationPart.builder()
                .amount(transactionEntity.getAmount())
                .categories(categories)
                .categoryType(transactionEntity.getCategoryType())
                .createdAt(transactionEntity.getCreatedAt())
                .currency(transactionEntity.getCurrency())
                .description(transactionEntity.getDescription())
                .name(transactionEntity.getName())
                .transactionId(transactionEntity.getTransactionId())
                .build();
    }


    public TransactionRecurrenceInformation fromTransactionEntityAndTransactionRecurrenceRuleEntityToTransactionRecurrenceInformation(TransactionEntity transactionEntity, TransactionRecurrenceRuleEntity transactionRecurrenceRuleEntity){

        return TransactionRecurrenceInformation.builder()
                .description(transactionEntity.getDescription())
                .name(transactionEntity.getName())
                .transactionId(transactionEntity.getTransactionId())
                .intervalCount(transactionRecurrenceRuleEntity.getIntervalCount())
                .lastRunDate(transactionRecurrenceRuleEntity.getLastRunDate())
                .maxOccurrences(transactionRecurrenceRuleEntity.getMaxOccurrences())
                .nextRunDate(transactionRecurrenceRuleEntity.getNextRunDate())
                .occurrencesGenerated(transactionRecurrenceRuleEntity.getOccurrencesGenerated())
                .recurrenceRuleId(transactionRecurrenceRuleEntity.getRecurrenceRuleId())
                .recurrenceUnit(transactionRecurrenceRuleEntity.getRecurrenceUnit())
                .build();

    }

}
