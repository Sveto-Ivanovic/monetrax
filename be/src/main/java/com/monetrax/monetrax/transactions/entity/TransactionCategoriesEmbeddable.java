package com.monetrax.monetrax.transactions.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionCategoriesEmbeddable implements Serializable {
    private UUID transactionId;
    private UUID categoryId;
}
