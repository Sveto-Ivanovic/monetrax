package com.monetrax.monetrax.transactions.dto;

import com.monetrax.monetrax.transactions.entity.AdjustmentKind;
import com.monetrax.monetrax.transactions.entity.TransactionEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class TransactionAdditionalInfoInformation {
    private UUID transactionInfoId;
    private AdjustmentKind kind;
    private String label;
    private BigDecimal amount;
}
