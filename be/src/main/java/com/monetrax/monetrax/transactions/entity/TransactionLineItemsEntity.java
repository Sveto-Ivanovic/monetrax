package com.monetrax.monetrax.transactions.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "transaction_line_items")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionLineItemsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "line_item_id")
    private UUID lineItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private TransactionEntity transaction;

    @Column(name = "product_name", length = 150)
    private String productName;

    @Column(name = "amount",  precision = 14, scale = 2)
    private BigDecimal amount;
}
