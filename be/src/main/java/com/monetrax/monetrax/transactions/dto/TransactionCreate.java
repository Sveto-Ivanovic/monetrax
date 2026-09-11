package com.monetrax.monetrax.transactions.dto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionCreate {
    @NotNull
    @NotBlank(message = "Name must be present.")
    @Size(min = 4, max = 100, message = "Size of the name must be between 4 and 100 characters.")
    private String name;

    @NotNull
    @NotBlank(message = "Label must be present.")
    @Size(min = 4, max = 250, message = "Size of the name must be between 4 and 100 characters.")
    private String description;

    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0.")
    private BigDecimal amount;

    @NotNull
    @NotBlank(message = "Currency must be present.")
    @Size(min = 3, max = 3, message = "Size of the currency must be 3 characters.")
    private String currency;

    @NotNull
    private List<RequestedCategoryInformation> categories;

    @NotNull
    private List<TransactionAdditionalInfoCreate> additionalInfo;

    @NotNull
    private List<TransactionLineItemsCreate> lineInformation;
}

class RequestedCategoryInformation {
    private UUID categoryId;
    private String name;
}
