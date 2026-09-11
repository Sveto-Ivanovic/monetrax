package com.monetrax.monetrax.transactions.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public class TransactionUpdate {

    @Size(min = 4, max = 100, message = "Size of the name must be between 4 and 100 characters.")
    private String name;

    @Size(min = 4, max = 250, message = "Size of the name must be between 4 and 100 characters.")
    private String description;

    @DecimalMin(value = "0.01", message = "Amount must be greater than 0.")
    private BigDecimal amount;

    @Size(min = 3, max = 3, message = "Size of the currency must be 3 characters.")
    private String currency;

    private List<RequestedCategoryInformation> categories;
}
