# Alerts API

**Tag:** Alerts
**Base path:** `/alerts`
**Security:** `BearerAuth` required for all operations.
**Content type:** `application/json` for request bodies. Success status: `200 OK`.

## `GET /alerts/alert/{alert_id}/fetch`

**Operation:** `getAlert`
**Summary:** Fetch one alert belonging to the authenticated user.

### Parameters

| Name | In | Type | Required |
| --- | --- | --- | --- |
| `alert_id` | path | `string (uuid)` | Yes |

### Responses

- **200** — `AlertInformation`

- **400/404** — Shared domain error response, depending on failure.

- **401** — Shared error response.

## `GET /alerts/account/{account_id}/fetch`

**Operation:** `getAccountAlerts`
**Summary:** List alerts associated with an account.

### Parameters

| Name | In | Type | Required |
| --- | --- | --- | --- |
| `account_id` | path | `string (uuid)` | Yes |

### Responses

- **200** — `AlertInformation[]`

- **401** — Shared error response.

## `POST /alerts/account/{account_id}/alert/create`

**Operation:** `createAlert`
**Summary:** Create an alert for an account.

### Parameters

| Name | In | Type | Required |
| --- | --- | --- | --- |
| `account_id` | path | `string (uuid)` | Yes |

### Request body — `AlertCreate` (required)

```json
{
  "name": "Monthly food budget",
  "description": "Notify when spending approaches the limit",
  "dateFrom": "2026-10-01",
  "dateTo": "2026-11-30",
  "filtersToCreate": [
    {
      "categoryId": "00000000-0000-0000-0000-000000000001",
      "ruleType": "GREATER_OR_EQUAL",
      "limitValueLowOrEqual": 500.00
    }
  ],
  "alertRecurrenceRule": null
}
```

| Property | Type | Required | Constraints |
| --- | --- | --- | --- |
| `name` | string | Yes | 4–50 characters |
| `description` | string | No | 15–250 characters |
| `dateFrom` | string (date) | Yes | ISO-8601 date |
| `dateTo` | string (date) | Yes | ISO-8601 date; must be in the future |
| `filtersToCreate` | `AlertConditionCreation[]` | Yes | Alert conditions |
| `alertRecurrenceRule` | `AlertRecurrenceRule` | No | See schema below |

### Responses

- **200** — `AlertCreateUpdateDeleteResponse`

- **400** — Validation or invalid-input error.

- **401** — Shared error response.

## `PATCH /alerts/alert/{alert_id}/update`

**Operation:** `updateAlert`
**Summary:** Update an alert's name, description, and condition filters.

### Parameters

| Name | In | Type | Required |
| --- | --- | --- | --- |
| `alert_id` | path | `string (uuid)` | Yes |

### Request body — `AlertUpdate` (required)

| Property | Type | Required | Constraints |
| --- | --- | --- | --- |
| `name` | string | Yes | 4–50 characters |
| `description` | string | No | 15–250 characters |
| `filtersToCreate` | `AlertConditionCreation[]` | Yes | Conditions replacing/updating the alert's filters |

### Responses

- **200** — `AlertCreateUpdateDeleteResponse`

- **400** — Validation or invalid-input error.

- **401** — Shared error response.

## `DELETE /alerts/alert/{alert_id}/delete`

**Operation:** `deleteAlert`
**Summary:** Delete an alert.

### Parameters

| Name | In | Type | Required |
| --- | --- | --- | --- |
| `alert_id` | path | `string (uuid)` | Yes |

### Responses

- **200** — `AlertCreateUpdateDeleteResponse`

- **401** — Shared error response.

## Schemas

**`AlertConditionCreation`** — `categoryId: uuid` (required), `ruleType: RuleType` (required), `limitValueLowOrEqual: number`, `limitValueHigh: number`. Present limits must be at least `0.01`. `RuleType`: `LESS_OR_EQUAL`, `GREATER_OR_EQUAL`, `BETWEEN`, `EQUAL`, `LESS`, `GREATER`.

**`AlertRecurrenceRule`** — `numberOfOccurrences: integer`, `isToEndOfTheMonth: boolean`, `ruleType: RecurrenceRuleType` (`EVERY_WEEK`, `EVERY_MONTH`, `EVERY_YEAR`, `EVERY_SPAN`), `recurrenceNum: integer`.

**`AlertCreateUpdateDeleteResponse`** — `msg: string`, `id: uuid`.

**`AlertInformation`** — `alertId: uuid`, `name: string`, `description: string`, `dateFrom: date`, `dateTo: date`, `filters: AlertConditionInformation[]`, `alertStates: AlertState[]`, `breached: boolean`, `active: boolean`.

**`AlertConditionInformation`** — `conditionId: uuid`, `categoryId: uuid`, `categoryName: string`, `ruleType: RuleType`, `limitValueLowOrEqual: number`, `limitValueHigh: number`.

**`AlertState`** — `categoryId: uuid`, `categoryName: string`, `amount: number`, `numOfTransactions: integer`, `avgPerTransaction: number`.

**Error response** — `status: integer`, `errors: [{ clue?: string, field?: string, message: string }]`.