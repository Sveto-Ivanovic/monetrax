package com.monetrax.monetrax.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardAnalyticsResponse {
    private List<TotalBalanceMonthly> totalBalanceMonthlyList;
    private List<MonthlyExpenses> monthlyExpenses;
    private List<MonthlyIncome> monthlyIncomes;
    private List<AccountBalance> accountBalances;
    private List<LatestTransactions> latestTransactions;
    private List<CategoryAmounts> categoryAmountsIncome;
    private List<CategoryAmounts> categoryAmountsExpense;
}
