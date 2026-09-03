package com.monetrax.monetrax.categories.dto;

import com.monetrax.monetrax.categories.entity.CategoryKind;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CategoryUpdate {

    private CategoryKind categoryType;

    @Size(min = 4, max = 50, message = "Size of the name must be between 4 and 50 characters.")
    private String name;

    @Size(min = 6, max = 250, message = "Size of the description must be between 6 and 250 characters.")
    private String description;

}
