package com.monetrax.monetrax.transactions.dto;

import com.monetrax.monetrax.transactions.entity.AdjustmentKind;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionAdditionalInfoCreate {
    @NotNull
    private AdjustmentKind kind;

    @NotNull
    @NotBlank(message = "Label must be present.")
    @Size(min = 4, max = 100, message = "Size of the label must be between 4 and 100 characters.")
    private String label;

    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0.")
    private BigDecimal amount;
}
