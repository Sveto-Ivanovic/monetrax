package com.monetrax.monetrax.transactions.entity;

import com.monetrax.monetrax.transactions.dto.TransactionRecurrenceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "transaction_recurrence_rules")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionRecurrenceRuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "recurrence_rule_id", updatable = false, nullable = false)
    private UUID recurrenceRuleId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_transaction_id", nullable = false)
    private TransactionEntity sourceTransaction;

    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_unit", nullable = false, length = 10)
    private TransactionRecurrenceType recurrenceUnit;

    @Column(name = "interval_count", nullable = false)
    private int intervalCount;

    @Column(name = "last_run_date")
    private LocalDate lastRunDate;

    @Column(name = "max_occurrences")
    private int maxOccurrences;

    @Column(name = "occurrences_generated", nullable = false)
    private int occurrencesGenerated = 0;

    @Column(name = "next_run_date", nullable = false)
    private LocalDate nextRunDate;
}
