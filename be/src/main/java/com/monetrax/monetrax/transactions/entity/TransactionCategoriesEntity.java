package com.monetrax.monetrax.transactions.entity;

import com.monetrax.monetrax.categories.entity.CategoryEntity;
import jakarta.persistence.*;


public class TransactionCategoriesEntity {
    @EmbeddedId
    private TransactionCategoriesEmbeddable id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("transactionId")
    @JoinColumn(name = "transaction_id")
    private TransactionEntity transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")
    @JoinColumn(name = "category_id")
    private CategoryEntity category;
}
