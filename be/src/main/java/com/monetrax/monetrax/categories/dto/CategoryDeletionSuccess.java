package com.monetrax.monetrax.categories.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CategoryDeletionSuccess {
    private String message;
    private boolean successfulDeletionOfCategory;
}
