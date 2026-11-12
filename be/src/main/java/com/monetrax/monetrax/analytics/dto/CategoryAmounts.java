package com.monetrax.monetrax.analytics.dto;

import com.monetrax.monetrax.categories.entity.CategoryKind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryAmounts {
    private String categoryName;
    private CategoryKind categoryKind;
    private BigDecimal amount;
}
