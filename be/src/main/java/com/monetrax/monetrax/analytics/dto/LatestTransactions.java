package com.monetrax.monetrax.analytics.dto;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LatestTransactions {
    private BigDecimal amount;
    private String currency;
    private String transactionName;
    private OffsetDateTime dateTimeZone;
    private List<String> categories;
    private CategoryKind categoryKind;
    private UUID transactionId;
    private String accountName;
}
