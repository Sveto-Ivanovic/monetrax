# Analytics API

**Tag:** Analytics
**Base path:** `/analytics`
**Security:** `BearerAuth` required.
**Content type:** `application/json`.

## `POST /analytics/fetch`

**Operation:** `fetchAnalytics`
**Summary:** Aggregate the authenticated user's transactions by time period and account.

### Request body — `AnalyticsRequest` (required)

```json
{
  "dateFrom": "2026-01-01",
  "dateTo": "2026-12-31",
  "accountIds": ["00000000-0000-0000-0000-000000000001"],
  "groupBy": "MONTH"
}
```

| Property | Type | Required | Description |
| --- | --- | --- | --- |
| `dateFrom` | string (date) | Yes | Start date, ISO-8601 |
| `dateTo` | string (date) | Yes | End date, ISO-8601 |
| `accountIds` | string (uuid)[] | No | Restrict aggregation to these accounts |
| `groupBy` | `GroupByTypes` | Yes | `DAY`, `MONTH`, or `YEAR` |

### Responses

- **200** — `AnalyticsResponse`

- **400** — Validation error.

- **401** — Shared error response.

## Schemas

**`AnalyticsResponse`** — `listOfTransactionsByCategoryKindAggregated: AccountTransactionsByCategoryKindAndCategoryAggregated[]`.

**`AccountTransactionsByCategoryKindAndCategoryAggregated`** — `accountId: uuid`, `accountName: string`, `state: number`, `income: CategoryKindAmount[]`, `expense: CategoryKindAmount[]`, `transferFrom: CategoryKindAmount[]`, `transferTo: CategoryKindAmount[]`, `adjustmentPlus: CategoryKindAmount[]`, `adjustmentMinus: CategoryKindAmount[]`, `categoryKindAmmountListList: CategoryAmount[]`, `uniqueCategoryIds: uuid[]`, `uniqueCategoryNames: string[]`, `productsList: Products[]`.

**`CategoryKindAmount`** — `categoryKind: CategoryKind`, `amount: number`, `xAxisData: string` (period key: date for day, year-month for month, year for year).

**`CategoryAmount`** — `categoryName: string`, `categoryId: uuid`, `amount: number[]`, `xAxisData: string[]`.

**`Products`** — `productName: string`, `amount: number`, `numberOfPurchases: integer`.

`CategoryKind` values: `INCOME`, `EXPENSE`, `TRANSFER_FROM`, `TRANSFER_TO`, `ADJUSTMENT_PLUS`, `ADJUSTMENT_MINUS`. Monetary values serialize as JSON numbers.

**Error response** — `status: integer`, `errors: [{ clue?: string, field?: string, message: string }]`.