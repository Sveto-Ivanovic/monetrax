package com.monetrax.monetrax.transactions.dto;

import com.monetrax.monetrax.alerts.dto.RecurrenceRuleType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRecurrenceRule {

    @NotNull
    private TransactionRecurrenceType ruleType;

    @Positive(message = "recurrenceNum must be at least 1")
    @Max(value = 99, message = "recurrenceNum must be less than 100")
    private int recurrenceNum;

    @Positive(message = "maxNumOfOccurrencesAllowed must be at least 1 if provided")
    @Max(value = 99, message = "maxNumOfOccurrencesAllowed must be less than 100")
    private Integer maxNumOfOccurrencesAllowed;
}