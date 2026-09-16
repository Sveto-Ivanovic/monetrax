package com.monetrax.monetrax.transactions.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "transaction_additional_info")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TransactionAdditionalInfoEntity {
    @Id
    @GeneratedValue
    @Column(name = "transaction_info_id")
    private UUID transactionInfoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private TransactionEntity transaction;

    @Enumerated(EnumType.STRING)
    @Column(name = "kind", length = 50)
    private AdjustmentKind kind;

    @Column(name = "label", length = 100)
    private String label;

    @Column(name = "amount", precision = 14, scale = 2)
    private BigDecimal amount;
}
