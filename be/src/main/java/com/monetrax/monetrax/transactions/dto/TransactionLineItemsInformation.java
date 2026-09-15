package com.monetrax.monetrax.transactions.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;


@AllArgsConstructor
@Data
@Builder
public class TransactionLineItemsInformation {
    private UUID lineItemId;
    private String productName;
    private BigDecimal amount;
}
