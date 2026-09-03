package com.monetrax.monetrax.categories.dto;

import com.monetrax.monetrax.categories.entity.CategoryKind;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CategoryInformation {
    private UUID categoryId;
    private CategoryKind categoryType;
    private String name;
    private String description;
    private boolean isDefault;
}
