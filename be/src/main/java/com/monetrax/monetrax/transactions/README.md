# Transactions API

**Tag:** Transactions
**Base path:** `/transactions`
**Security:** `BearerAuth` required for all operations.
**Content type:** `application/json` for request bodies. All success responses below are HTTP `200 OK`.

## `POST /transactions/account/{account_id}/transaction/create`

**Operation:** `createTransaction`
**Summary:** Create a transaction in an account.

### Parameters

| Name | In | Type | Required |
| --- | --- | --- | --- |
| `account_id` | path | `string (uuid)` | Yes |

### Request body — `TransactionCreate` (required)

```json
{
  "name": "Groceries",
  "description": "Weekly grocery shop",
  "amount": 42.50,
  "currency": "USD",
  "customCreationDate": "2026-10-06T12:00:00Z",
  "categories": [{ "categoryId": "00000000-0000-0000-0000-000000000001", "name": "Groceries" }],
  "additionalInfo": [],
  "lineInformation": [{ "productName": "Produce", "amount": 20.00 }],
  "transactionRecurrenceRule": null
}
```

`name` (4–100 chars), `description` (4–250 chars), positive `amount`, 3-char `currency`, and arrays `categories`, `additionalInfo`, `lineInformation` are required. `customCreationDate` is optional and must be a past offset datetime. Recurrence is optional.

### Responses

- **200** — `TransactionCreateUpdateResponse`

- **400** — Validation or invalid transaction request.

- **401** — Shared error response.

## `GET /transactions/transaction/{transaction_id}/fetch`

**Operation:** `getTransaction`
**Summary:** Fetch a transaction and its categories, adjustments, and line items.

### Parameters

| Name | In | Type | Required |
| --- | --- | --- | --- |
| `transaction_id` | path | `string (uuid)` | Yes |

### Responses

- **200** — `TransactionInformation`

- **400** — Invalid transaction identifier or domain error.

## `GET /transactions/account/{account_id}/fetch`

**Operation:** `getAccountTransactions`
**Summary:** Fetch account information and its transaction list.

### Parameters

| Name | In | Type | Required |
| --- | --- | --- | --- |
| `account_id` | path | `string (uuid)` | Yes |

### Responses

- **200** — `ListOfAccountTransactions`

- **400** — Invalid account identifier or domain error.

## `PUT /transactions/transaction/{transaction_id}/update`

**Operation:** `updateTransaction`
**Summary:** Update transaction fields.

### Parameters

| Name | In | Type | Required |
| --- | --- | --- | --- |
| `transaction_id` | path | `string (uuid)` | Yes |

### Request body — `TransactionUpdate` (required)

Provide one or more fields: `name` (4–100 chars), `description` (4–250 chars), `amount` (minimum 0.01), `conversionFactor` (minimum 0.01), `categories` (`RequestedCategoryInformation[]`).

### Responses

- **200** — `TransactionCreateUpdateResponse`

- **400** — Validation failure or no meaningful fields supplied.

## `DELETE /transactions/transaction/{transaction_id}/delete`

**Operation:** `deleteTransaction`
**Summary:** Delete a transaction.

### Parameters

| Name | In | Type | Required |
| --- | --- | --- | --- |
| `transaction_id` | path | `string (uuid)` | Yes |

### Responses

- **200** — `TransactionCreateUpdateResponse`

- **403** — Deletion/state conflict.

## `POST /transactions/transaction/{transaction_id}/transaction-additional-info/create`

**Operation:** `createTransactionAdditionalInfo`
**Summary:** Add an adjustment/extra amount to a transaction.

### Parameters

| Name | In | Type | Required |
| --- | --- | --- | --- |
| `transaction_id` | path | `string (uuid)` | Yes |

### Request body — `TransactionAdditionalInfoCreate` (required)

```json
{ "kind": "ADDITION", "label": "Cashback", "amount": 5.00 }
```

Fields: `kind` (`DEDUCTION` or `ADDITION`), `label` (4–100 chars), positive `amount`.

### Responses

- **200** — `TransactionCreateUpdateResponse`

- **400** — Validation or domain error.

## `DELETE /transactions/transaction/{transaction_id}/transaction-additional-info/{transaction_additional_id}`

**Operation:** `deleteTransactionAdditionalInfo`
**Summary:** Remove an additional-info item.

### Parameters

| Name | In | Type | Required |
| --- | --- | --- | --- |
| `transaction_id` | path | `string (uuid)` | Yes |
| `transaction_additional_id` | path | `string (uuid)` | Yes |

### Responses

- **200** — `TransactionCreateUpdateResponse`

- **400** — Invalid IDs or domain error.

## `POST /transactions/transaction/{transaction_id}/transaction-line-product/create`

**Operation:** `createTransactionLineItem`
**Summary:** Add a product/line item to a transaction.

### Parameters

| Name | In | Type | Required |
| --- | --- | --- | --- |
| `transaction_id` | path | `string (uuid)` | Yes |

### Request body — `TransactionLineItemsCreate` (required)

```json
{ "productName": "Coffee beans", "amount": 12.50 }
```

Fields: `productName` (4–100 chars), positive `amount`.

### Responses

- **200** — `TransactionCreateUpdateResponse`

- **400** — Validation or domain error.

## `DELETE /transactions/transaction/{transaction_id}/transaction-line-product/{transaction_line_id}`

**Operation:** `deleteTransactionLineItem`
**Summary:** Remove a product/line item from a transaction.

### Parameters

| Name | In | Type | Required |
| --- | --- | --- | --- |
| `transaction_id` | path | `string (uuid)` | Yes |
| `transaction_line_id` | path | `string (uuid)` | Yes |

### Responses

- **200** — `TransactionCreateUpdateResponse`

- **400** — Invalid IDs or domain error.

## `GET /transactions/account/{account_id}/transaction-recurrence-rule/fetch`

**Operation:** `getAccountTransactionRules`
**Summary:** List transaction recurrence rules for an account.

### Parameters

| Name | In | Type | Required |
| --- | --- | --- | --- |
| `account_id` | path | `string (uuid)` | Yes |

### Responses

- **200** — `TransactionRecurrenceResponse`

- **400** — Invalid account identifier or domain error.

## `DELETE /transactions/transaction/{transaction_id}/transaction-recurrence-rule/{transaction_rule_id}`

**Operation:** `deleteTransactionRecurrenceRule`
**Summary:** Remove a recurrence rule from a transaction.

### Parameters

| Name | In | Type | Required |
| --- | --- | --- | --- |
| `transaction_id` | path | `string (uuid)` | Yes |
| `transaction_rule_id` | path | `string (uuid)` | Yes |

### Responses

- **200** — `TransactionCreateUpdateResponse`

- **400** — Invalid IDs or domain error.

## `POST /transactions/ai/account/{account_id}/transaction/create`

**Operation:** `createTransactionWithAi`
**Summary:** Extract transaction details from user text using the requested Gemini model and create the transaction.

### Parameters

| Name | In | Type | Required |
| --- | --- | --- | --- |
| `account_id` | path | `string (uuid)` | Yes |

### Request body — `TransactionCreateAIRequest` (required)

| Property | Type | Required | Constraints |
| --- | --- | --- | --- |
| `msg` | string | Yes | 10–4000 characters |
| `customCreationDate` | string (date-time) | No | Must be in the past |
| `geminiModel` | Google `ChatModel` enum | Yes | Spring AI-supported model value |
| `categories` | `RequestedCategoryInformation[]` | Yes | Each item has category UUID and name |
| `transactionRecurrenceRule` | `TransactionRecurrenceRule` | No | See schema below |

### Responses

- **200** — `TransactionCreateUpdateResponse`

- **400** — Validation, AI provider, or transaction creation error.

## Schemas

**`TransactionCreateUpdateResponse`** — `msg: string`, `transactionId: uuid`.

**`RequestedCategoryInformation`** — `categoryId: uuid` (required), `name: string` (required).

**`TransactionRecurrenceRule`** — `ruleType: TransactionRecurrenceType` (`DAY`, `WEEK`, `MONTH`, `YEAR`), `recurrenceNum: integer` (1–99), `maxNumOfOccurrencesAllowed: integer` (optional, 1–99).

**`TransactionInformation`** — `transactionId: uuid`, `name: string`, `description: string`, `amount: number`, `currency: string`, `categoryType: CategoryKind`, `createdAt: date-time`, `nativeAmount: number`, `conversionFactor: number`, `categories: CategoryInformation[]`, `additionalInfo: TransactionAdditionalInfoInformation[]`, `lineItemsInformation: TransactionLineItemsInformation[]`.

**`ListOfAccountTransactions`** — `account: AccountInformation`, `transactions: TransactionInformationPart[]`.

**`TransactionInformationPart`** — `transactionId: uuid`, `name: string`, `description: string`, `amount: number`, `currency: string`, `categoryType: CategoryKind`, `createdAt: date-time`, `categories: string[]`.

**`TransactionAdditionalInfoInformation`** — `transactionInfoId: uuid`, `kind: AdjustmentKind`, `label: string`, `amount: number`.

**`TransactionLineItemsInformation`** — `lineItemId: uuid`, `productName: string`, `amount: number`.

**`TransactionRecurrenceResponse`** — `list: TransactionRecurrenceInformation[]`. Each item contains `transactionId`, `name`, `description`, `recurrenceRuleId`, `recurrenceUnit`, `intervalCount`, `lastRunDate`, `maxOccurrences`, `occurrencesGenerated`, `nextRunDate`.

**Shared error response** — `status: integer`, `errors: [{ clue?: string, field?: string, message: string }]`.