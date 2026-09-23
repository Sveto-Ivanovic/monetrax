package com.monetrax.monetrax.alerts.dto;

import com.monetrax.monetrax.alerts.entity.RuleType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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
public class AlertConditionCreation {
    @NotNull
    private UUID categoryId;

    @NotNull
    private RuleType ruleType;

    @DecimalMin(value = "0.01", message = "Amount must be greater than 0.")
    private BigDecimal limitValueLowOrEqual;

    @DecimalMin(value = "0.01", message = "Amount must be greater than 0.")
    private BigDecimal limitValueHigh;
}
