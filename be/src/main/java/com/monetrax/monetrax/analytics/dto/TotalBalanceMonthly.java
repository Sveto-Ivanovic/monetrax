package com.monetrax.monetrax.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TotalBalanceMonthly {
    private BigDecimal totalBalance;
    private String currency;
    private String month;
}
