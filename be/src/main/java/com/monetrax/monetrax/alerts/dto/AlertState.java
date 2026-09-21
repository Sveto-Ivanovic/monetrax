package com.monetrax.monetrax.alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlertState {
    private UUID categoryId;
    private String categoryName;
    private BigDecimal amount;
    private int numOfTransactions;
    private BigDecimal avgPerTransaction;
}
