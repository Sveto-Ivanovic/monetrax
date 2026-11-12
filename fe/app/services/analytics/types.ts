export interface FetchedTemplateTest {
    staticDataTotalBalance: MonthlyBalance[];
    staticDataTotalExpenses: MonthlyExpenses[];
    staticDataTotalIncome: MonthlyIncome[];
    totalBalanceByAccount: AccountBalance[];
    latestFiveTransactions: Transaction[];
    categoryAmountsIncome?: CategoryAmounts[];
    categoryAmountsExpense?: CategoryAmounts[];
}

export interface FetchedDashboardAnalyticsData {
    staticDataTotalBalance: MonthlyBalance[];
    staticDataTotalExpenses: MonthlyExpenses[];
    staticDataTotalIncome: MonthlyIncome[];
    totalBalanceByAccount: AccountBalance[];
    latestFiveTransactions: Transaction[];
    categoryAmountsIncome: CategoryAmounts[];
    categoryAmountsExpense: CategoryAmounts[];
}

export interface MonthlyBalance {
    totalBalance: number;
    currency: string;
    month: string;
}

export interface MonthlyExpenses {
    totalExpenses: number;
    currency: string;
    month: string;
}

export interface MonthlyIncome {
    totalIncome: number;
    currency: string;
    month: string;
}

export interface AccountBalance {
    accountName: string;
    bankNumLast4: string;
    totalBalance: number;
    currency: string;
    id: string;
    institution: string;
}

export interface Transaction {
    transactionName: string;
    dateTimeZone: string;
    amount: number;
    currency: string;
    categories: string[];
    categoryKind: TransactionCategoryKind;
    transactionId?: String;
    accountName?: String;
}

export interface CategoryAmounts {
    categoryName: String;
    categoryKind: TransactionCategoryKind;
    amount: number;
}


export type TransactionCategoryKind = 'EXPENSE' | 'INCOME' | 'TRANSFER_FROM' |
    'TRANSFER_TO' |
    'ADJUSTMENT_PLUS' |
    'ADJUSTMENT_MINUS';