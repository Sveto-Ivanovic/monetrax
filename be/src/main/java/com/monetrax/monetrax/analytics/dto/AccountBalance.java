package com.monetrax.monetrax.analytics.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountBalance {
    private BigDecimal totalBalance;
    private String currency;
    private String accountName;
    private String bankNumLast4;
    private UUID id;
    private String institution;
}
