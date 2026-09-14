package com.monetrax.monetrax.transactions.dto;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "categoryId")
public class RequestedCategoryInformation {
    private UUID categoryId;
    private String name;


}