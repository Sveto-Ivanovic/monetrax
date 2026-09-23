package com.monetrax.monetrax.alerts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlertRecurrenceRule {
    private int numberOfOccurrences;
    private Boolean isToEndOfTheMonth;
    private RecurrenceRuleType ruleType;
    // let's say if rule type is month, recurrenceNum would be every 2, 3, 4, 5 moths etc.
    private int recurrenceNum;
}
