package com.monetrax.monetrax.accounts.dto;

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
public class AccountUpdate {

    @Size(min = 5, max = 100)
    private String name;

    @Size(min = 6, max = 254)
    private String description;

    @Size(min = 5, max = 100)
    private String institutionName;

    @Size(min = 4, max = 4)
    private String accountNumberMasked;

    private Boolean toggleActivate;
}
