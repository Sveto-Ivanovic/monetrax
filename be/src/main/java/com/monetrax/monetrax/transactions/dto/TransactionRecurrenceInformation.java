package com.monetrax.monetrax.transactions.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRecurrenceInformation {
    public UUID transactionId;

    public String name;

    public  String description;

    private UUID recurrenceRuleId;

    private TransactionRecurrenceType recurrenceUnit;

    private int intervalCount;

    private LocalDate lastRunDate;

    private int maxOccurrences;

    private int occurrencesGenerated;

    private LocalDate nextRunDate;
}
