package com.monetrax.monetrax.analytics.dto;

import com.monetrax.monetrax.categories.entity.CategoryKind;
import com.monetrax.monetrax.transactions.dto.TransactionInformationPart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnalyticsResponse {

        private UUID accountId;
        private String accountName;
        private BigDecimal state;
        private String currency;


        // specific to the pure analytics part not the comparison part of the page
        private BigDecimal incomeTotal;
        private BigDecimal expenseTotal;
        private BigDecimal net;

        private List<CategoryKindAmount> income;
        private List<CategoryKindAmount> expense;

        private List<CategoryState> categoryStateListExpense;
        private List<CategoryState> categoryStateListIncome;

        private List<TransactionInformationPart> transactionInformationPartList;
        private List<Products> productsList;


        // comparison part, all strings down, in the map, the keys specifically are local date with months days and year
        private Map<String, BigDecimal> mapDateAmountIncome;
        private Map<String, BigDecimal> mapDateAmountExpense;
        private Map<String, BigDecimal> mapDateAmountNet;

        private Map<String, List<CategoryState>> stringListMapCategoriesByDate;
        private Map<String, List<Products>> stringListMapProductByDate;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static  class CategoryKindAmount{
       private CategoryKind categoryKind;
       private  BigDecimal amount;
       // depending on group by
       // LocalDate → 2026-10-02
       private String xAxisData;
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



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static  class CategoryState{
        private String categoryName;
        private UUID categoryId;
        private BigDecimal amount;
    }



}
