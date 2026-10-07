package com.monetrax.monetrax.transactions.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionExtraction {

    private String name;
    private String description;
    private BigDecimal amount;
    private String currency;
    private List<@Valid TransactionLineItemsCreate> lineInformation;
    private String error;
}