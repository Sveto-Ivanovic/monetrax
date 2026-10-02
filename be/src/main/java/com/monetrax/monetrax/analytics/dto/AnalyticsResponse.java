package com.monetrax.monetrax.analytics.dto;

import com.monetrax.monetrax.categories.entity.CategoryKind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnalyticsResponse {

    private List<AccountTransactionsByCategoryKindAndCategoryAggregated>  listOfTransactionsByCategoryKindAggregated;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AccountTransactionsByCategoryKindAndCategoryAggregated{
        private UUID accountId;
        private String accountName;
        private BigDecimal state;

        private List<CategoryKindAmount> income;
        private List<CategoryKindAmount> expense;
        private List<CategoryKindAmount> transferFrom;
        private List<CategoryKindAmount> transferTo;
        private List<CategoryKindAmount> adjustmentPlus;
        private List<CategoryKindAmount> adjustmentMinus;

        private List<CategoryAmount> categoryKindAmmountListList;
        private List<UUID> uniqueCategoryIds;
        private List<String> uniqueCategoryNames;

        private List<Products> productsList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static  class CategoryKindAmount{
       private CategoryKind categoryKind;
       private  BigDecimal amount;
       // depending on group by
       // YearMonth → 2026-10
       // Year → 2026
       // LocalDate → 2026-10-02
       private String xAxisData;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static  class CategoryAmount{
        private String categoryName;
        private UUID categoryId;
        private List<BigDecimal> amount;
        private List<String> xAxisData;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static  class Products{
        private String productName;
        private  BigDecimal amount;
        private int numberOfPurchases;
    }







}
