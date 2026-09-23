package com.monetrax.monetrax.alerts.dto;

import com.monetrax.monetrax.alerts.entity.RuleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertConditionUpdate {
    private UUID categoryId;

    private RuleType ruleType;

    private BigDecimal limitValueLowOrEqual;

    private BigDecimal limitValueHigh;
}
