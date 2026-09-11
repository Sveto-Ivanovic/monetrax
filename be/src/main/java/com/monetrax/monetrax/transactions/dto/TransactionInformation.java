package com.monetrax.monetrax.transactions.dto;

import com.monetrax.monetrax.categories.dto.CategoryInformation;
import com.monetrax.monetrax.categories.entity.CategoryKind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionInformation {
    private UUID transactionId;
    private String name;
    private String description;
    private BigDecimal amount;
    private String currency;
    private CategoryKind categoryType;
    private OffsetDateTime createdAt;
    private List<CategoryInformation> categories;
    private List<TransactionAdditionalInfoInformation> additionalInfo;
    private List<TransactionLineItemsInformation> lineItemsInformation;
}
