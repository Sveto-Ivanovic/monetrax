import type { ListOfAccountTransactions } from "~/services/transactions/types";

export const accountTransactionsExamples: ListOfAccountTransactions = 
  {
    account: {
      accountId: 'a1111111-1111-4111-8111-111111111111',
      name: 'Main Checking',
      description: 'Everyday spending account',
      currentBalance: 2845.37,
      currency: 'EUR',
      institutionName: 'Erste Bank',
      accountNumberMasked: '4821',
      active: true,
    },
    transactions: [
      { transactionId: 't0000001-0000-4000-8000-000000000001', name: 'November salary', description: 'ACME d.o.o. payroll', amount: 3200.0, currency: 'EUR', categoryType: 'INCOME', createdAt: '2026-11-01T08:00:00+01:00', categories: ['Salary'] },
      { transactionId: 't0000001-0000-4000-8000-000000000002', name: 'Rent November', description: null, amount: 900.0, currency: 'EUR', categoryType: 'EXPENSE', createdAt: '2026-11-02T09:15:00+01:00', categories: ['Rent'] },
      { transactionId: 't0000001-0000-4000-8000-000000000003', name: 'Voli Supermarket', description: 'Weekly shopping', amount: 54.8, currency: 'EUR', categoryType: 'EXPENSE', createdAt: '2026-11-03T17:42:00+01:00', categories: ['Groceries'] },
      { transactionId: 't0000001-0000-4000-8000-000000000004', name: 'Pizzeria Napoli', description: null, amount: 23.9, currency: 'EUR', categoryType: 'EXPENSE', createdAt: '2026-11-05T20:05:00+01:00', categories: ['Dining out', 'Entertainment'] },
      { transactionId: 't0000001-0000-4000-8000-000000000005', name: 'Netflix', description: null, amount: 12.99, currency: 'EUR', categoryType: 'EXPENSE', createdAt: '2026-11-06T03:00:00+01:00', categories: ['Entertainment'] },
      { transactionId: 't0000001-0000-4000-8000-000000000006', name: 'To savings', description: 'Monthly savings', amount: 500.0, currency: 'EUR', categoryType: 'TRANSFER_TO', createdAt: '2026-11-08T12:00:00+01:00', categories: ['Transfer out'] },
      { transactionId: 't0000001-0000-4000-8000-000000000007', name: 'Freelance logo design', description: null, amount: 250.0, currency: 'EUR', categoryType: 'INCOME', createdAt: '2026-11-11T14:00:00+01:00', categories: ['Freelance'] },
      { transactionId: 't0000001-0000-4000-8000-000000000007', name: 'Freelance logo design', description: null, amount: 250.0, currency: 'EUR', categoryType: 'INCOME', createdAt: '2026-11-11T14:00:00+01:00', categories: ['Other Income'] },
      { transactionId: 't0000001-0000-4000-8000-000000000008', name: 'Cash correction', description: 'Missing cash receipt', amount: 5.0, currency: 'EUR', categoryType: 'ADJUSTMENT_MINUS', createdAt: '2026-11-13T09:00:00+01:00', categories: ['Balance correction'] },
         { transactionId: 't0000003-0000-4000-8000-000000000001', name: 'Top-up from checking', description: null, amount: 600.0, currency: 'USD', categoryType: 'TRANSFER_FROM', createdAt: '2026-10-01T10:00:00+02:00', categories: ['Transfer in'] },
      { transactionId: 't0000003-0000-4000-8000-000000000002', name: 'Flight Podgorica - Vienna', description: 'Return ticket', amount: 189.99, currency: 'USD', categoryType: 'EXPENSE', createdAt: '2026-10-02T06:30:00+02:00', categories: ['Travel', 'Transport'] },
      { transactionId: 't0000003-0000-4000-8000-000000000003', name: 'Hotel Wien Mitte', description: null, amount: 412.0, currency: 'USD', categoryType: 'EXPENSE', createdAt: '2026-10-03T14:00:00+02:00', categories: ['Travel'] },
      { transactionId: 't0000003-0000-4000-8000-000000000004', name: 'Cafe Central', description: null, amount: 28.5, currency: 'USD', categoryType: 'EXPENSE', createdAt: '2026-10-03T16:30:00+02:00', categories: ['Dining out'] },
      { transactionId: 't0000003-0000-4000-8000-000000000005', name: 'Schnitzel House', description: null, amount: 47.25, currency: 'USD', categoryType: 'EXPENSE', createdAt: '2026-10-04T20:45:00+02:00', categories: ['Dining out'] },
      { transactionId: 't0000003-0000-4000-8000-000000000006', name: 'Refund hotel deposit', description: null, amount: 50.0, currency: 'USD', categoryType: 'INCOME', createdAt: '2026-10-10T12:00:00+02:00', categories: ['Refunds'] },
      { transactionId: 't0000003-0000-4000-8000-000000000007', name: 'Unknown ATM fee', description: null, amount: 4.5, currency: 'USD', categoryType: 'ADJUSTMENT_MINUS', createdAt: '2026-10-06T08:00:00+02:00', categories: ['ATM'] },
      { transactionId: 't0000003-0000-4000-8000-000000000008', name: 'Rounding fix', description: null, amount: 0.01, currency: 'USD', categoryType: 'ADJUSTMENT_PLUS', createdAt: '2026-10-06T08:05:00+02:00', categories: ['Balance correction +'] },

   
    ],
    categoryInformationList: [
      { categoryId: 'c0000001-0000-4000-8000-000000000001', categoryType: 'INCOME', name: 'Salary', description: 'Monthly salary', defaultCategory: true },
      { categoryId: 'c0000001-0000-4000-8000-000000000002', categoryType: 'INCOME', name: 'Freelance', description: 'Side projects', defaultCategory: false },
      { categoryId: 'c0000001-0000-4000-8000-000000000003', categoryType: 'EXPENSE', name: 'Groceries', description: 'Supermarkets and markets', defaultCategory: true },
      { categoryId: 'c0000001-0000-4000-8000-000000000004', categoryType: 'EXPENSE', name: 'Rent', description: 'Apartment rent', defaultCategory: false },
      { categoryId: 'c0000001-0000-4000-8000-000000000005', categoryType: 'EXPENSE', name: 'Dining out', description: null, defaultCategory: false },
      { categoryId: 'c0000001-0000-4000-8000-000000000006', categoryType: 'EXPENSE', name: 'Entertainment', description: 'Cinema, games, streaming', defaultCategory: false },
      { categoryId: 'c0000001-0000-4000-8000-000000000007', categoryType: 'TRANSFER_TO', name: 'Transfer out', description: null, defaultCategory: true },
      { categoryId: 'c0000001-0000-4000-8000-000000000008', categoryType: 'ADJUSTMENT_MINUS', name: 'Balance correction -', description: null, defaultCategory: true },
           { categoryId: 'c0000003-0000-4000-8000-000000000001', categoryType: 'EXPENSE', name: 'Travel', description: 'Flights and hotels', defaultCategory: false },
      { categoryId: 'c0000003-0000-4000-8000-000000000002', categoryType: 'EXPENSE', name: 'Dining out', description: null, defaultCategory: false },
      { categoryId: 'c0000003-0000-4000-8000-000000000003', categoryType: 'EXPENSE', name: 'Transport', description: null, defaultCategory: false },
      { categoryId: 'c0000003-0000-4000-8000-000000000004', categoryType: 'TRANSFER_FROM', name: 'Transfer in', description: null, defaultCategory: true },
      { categoryId: 'c0000003-0000-4000-8000-000000000005', categoryType: 'INCOME', name: 'Refunds', description: null, defaultCategory: false },
      { categoryId: 'c0000003-0000-4000-8000-000000000006', categoryType: 'ADJUSTMENT_PLUS', name: 'Balance correction +', description: null, defaultCategory: true },

   
    ],
    listOfProducts: ['Milk', 'Bread', 'Eggs', 'Coffee beans', 'Pizza Margherita', 'Return ticket', 'Hotel night', 'Apple strudel', 'Melange', 'Schnitzel'],
    alerts: [
      {
        alertId: 'al000001-0000-4000-8000-000000000001',
        name: 'Monthly food budget',
        description: 'Groceries and dining should stay under budget',
        dateFrom: '2026-11-01',
        dateTo: '2026-11-30',
        filters: [
          { conditionId: 'cd000001-0000-4000-8000-000000000001', categoryId: 'c0000001-0000-4000-8000-000000000003', categoryName: 'Groceries', ruleType: 'LESS_OR_EQUAL', limitValueLowOrEqual: null, limitValueHigh: 150 },
          { conditionId: 'cd000001-0000-4000-8000-000000000002', categoryId: 'c0000001-0000-4000-8000-000000000005', categoryName: 'Dining out', ruleType: 'LESS_OR_EQUAL', limitValueLowOrEqual: null, limitValueHigh: 50 },
        ],
        alertStates: [
          { categoryId: 'c0000001-0000-4000-8000-000000000003', categoryKind: 'EXPENSE', categoryName: 'Groceries', amount: 54.8, numOfTransactions: 1, avgPerTransaction: 54.8 },
          { categoryId: 'c0000001-0000-4000-8000-000000000005', categoryKind: 'EXPENSE',categoryName: 'Dining out', amount: 23.9, numOfTransactions: 1, avgPerTransaction: 23.9 },
        ],
        breached: false,
        active: true,
      },
      {
        alertId: 'al000001-0000-4000-8000-000000000002',
        name: 'Entertainment cap',
        description: 'Keep entertainment under 10',
        dateFrom: '2026-11-01',
        dateTo: '2026-11-30',
        filters: [
          { conditionId: 'cd000001-0000-4000-8000-000000000003', categoryId: 'c0000001-0000-4000-8000-000000000006', categoryName: 'Entertainment', ruleType: 'LESS', limitValueLowOrEqual: null, limitValueHigh: 10 },
        ],
        alertStates: [
          { categoryId: 'c0000001-0000-4000-8000-000000000006',categoryKind: 'EXPENSE', categoryName: 'Entertainment', amount: 36.89, numOfTransactions: 2, avgPerTransaction: 18.45 },
        ],
        breached: true,
        active: true,
      },
       {
        alertId: 'al000003-0000-4000-8000-000000000001',
        name: 'Dining between 50 and 150',
        description: null,
        dateFrom: '2026-10-01',
        dateTo: '2026-10-31',
        filters: [
          { conditionId: 'cd000003-0000-4000-8000-000000000001', categoryId: 'c0000003-0000-4000-8000-000000000002', categoryName: 'Dining out', ruleType: 'BETWEEN', limitValueLowOrEqual: 50, limitValueHigh: 150 },
        ],
        alertStates: [
          { categoryId: 'c0000003-0000-4000-8000-000000000002',categoryKind: 'EXPENSE', categoryName: 'Dining out', amount: 75.75, numOfTransactions: 2, avgPerTransaction: 37.88 },
        ],
        breached: false,
        active: true,
      },
      {
        alertId: 'al000003-0000-4000-8000-000000000002',
        name: 'Travel under 500',
        description: 'Strict cap',
        dateFrom: '2026-10-01',
        dateTo: '2026-10-31',
        filters: [
          { conditionId: 'cd000003-0000-4000-8000-000000000002', categoryId: 'c0000003-0000-4000-8000-000000000001', categoryName: 'Travel', ruleType: 'LESS', limitValueLowOrEqual: null, limitValueHigh: 500 },
        ],
        alertStates: [
          { categoryId: 'c0000003-0000-4000-8000-000000000001', categoryKind: 'EXPENSE',categoryName: 'Travel', amount: 601.99, numOfTransactions: 2, avgPerTransaction: 301.0 },
        ],
        breached: true,
        active: true,
      },
      {
        alertId: 'al000003-0000-4000-8000-000000000003',
        name: 'Transport exactly 17.10 or more than 100',
        description: 'EQUAL + GREATER in one alert',
        dateFrom: '2026-10-01',
        dateTo: '2026-12-31',
        filters: [
          { conditionId: 'cd000003-0000-4000-8000-000000000003', categoryId: 'c0000003-0000-4000-8000-000000000003', categoryName: 'Transport', ruleType: 'EQUAL', limitValueLowOrEqual: 17.1, limitValueHigh: null },
          { conditionId: 'cd000003-0000-4000-8000-000000000004', categoryId: 'c0000003-0000-4000-8000-000000000003', categoryName: 'Transport', ruleType: 'GREATER', limitValueLowOrEqual: 100, limitValueHigh: null },
        ],
        alertStates: [
          { categoryId: 'c0000003-0000-4000-8000-000000000003', categoryKind: 'EXPENSE', categoryName: 'Transport', amount: 189.99, numOfTransactions: 1, avgPerTransaction: 189.99 },
        ],
        breached: true,
        active: false,
      },
    ],
  }
