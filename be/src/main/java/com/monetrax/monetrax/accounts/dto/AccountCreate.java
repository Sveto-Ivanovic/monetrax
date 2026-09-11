package com.monetrax.monetrax.accounts.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountCreate {

    @NotNull
    @NotBlank
    @Size(min = 5, max = 100)
    private String name;

    @Size(min = 6, max = 254)
    private String description;

    @NotNull
    @NotBlank
    @Size(min = 3, max = 3)
    private String currency;

    @NotNull
    @NotBlank
    @Size(min = 5, max = 100)
    private String institutionName;

    @Size(min = 4, max = 4)
    private String accountNumberMasked;

    private BigDecimal initialBalance;

}
