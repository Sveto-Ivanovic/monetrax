package com.monetrax.monetrax.transactions.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "categoryId")
public class RequestedCategoryInformation {
    @NotNull
    private UUID categoryId;
    @NotNull
    private String name;


}