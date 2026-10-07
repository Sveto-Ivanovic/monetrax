# Accounts API

**Tag:** Accounts
**Base path:** `/accounts`
**Security:** `BearerAuth` required for all operations.
**Content type:** `application/json` for request bodies. All successful operations return `200 OK`.

## `GET /accounts/account/{account_id}`

**Operation:** `getAccount`
**Summary:** Fetch one account owned by the authenticated user.

### Parameters

| Name | In | Type | Required | Description |
| --- | --- | --- | --- | --- |
| `account_id` | path | `string (uuid)` | Yes | Account identifier |

### Responses

- **200** — `AccountInformation`

- **404** — Shared error response when the account is not found.

- **401** — Shared error response when unauthenticated.

## `GET /accounts/all`

**Operation:** `getAccounts`
**Summary:** List accounts for the authenticated user.

### Parameters

| Name | In | Type | Required | Description |
| --- | --- | --- | --- | --- |
| `includeArchive` | query | `boolean` | No | Include archived accounts; defaults to `false` |

### Responses

- **200** — `AccountListResponse`

- **401** — Shared error response.

## `GET /accounts/all/archived`

**Operation:** `getArchivedAccounts`
**Summary:** List archived accounts for the authenticated user.

### Parameters

None.

### Responses

- **200** — `AccountListResponse`

- **401** — Shared error response.

## `POST /accounts/create`

**Operation:** `createAccount`
**Summary:** Create an account for the authenticated user.

### Request body — `AccountCreate` (required)

```json
{
  "name": "Everyday account",
  "description": "Primary spending account",
  "currency": "USD",
  "institutionName": "Example Bank",
  "accountNumberMasked": "1234",
  "initialBalance": 250.00
}
```

| Property | Type | Required | Constraints |
| --- | --- | --- | --- |
| `name` | string | Yes | 5–100 characters |
| `description` | string | No | 6–254 characters |
| `currency` | string | Yes | Exactly 3 characters |
| `institutionName` | string | Yes | 5–100 characters |
| `accountNumberMasked` | string | No | Exactly 4 characters |
| `initialBalance` | number | No | Decimal amount |

### Responses

- **200** — `AccountInformation`

- **400** — Validation error.

- **401** — Shared error response.

## `PUT /accounts/account/{account_id}/update`

**Operation:** `updateAccount`
**Summary:** Update account profile fields or activation state.

### Parameters

| Name | In | Type | Required | Description |
| --- | --- | --- | --- | --- |
| `account_id` | path | `string (uuid)` | Yes | Account identifier |

### Request body — `AccountUpdate` (required)

All fields are optional; provide one or more.

| Property | Type | Constraints |
| --- | --- | --- |
| `name` | string | 5–100 characters |
| `description` | string | 6–254 characters |
| `institutionName` | string | 5–100 characters |
| `accountNumberMasked` | string | Exactly 4 characters |
| `toggleActivate` | boolean | Toggle account activation |

### Responses

- **200** — `AccountInformation`

- **400** — Validation error.

- **404** — Account missing or no updatable data, as applicable.

## `PATCH /accounts/account/{account_id}/update/archive`

**Operation:** `setAccountArchiveState`
**Summary:** Archive or restore an account.

### Parameters

| Name | In | Type | Required | Description |
| --- | --- | --- | --- | --- |
| `account_id` | path | `string (uuid)` | Yes | Account identifier |

### Request body — `AccountArchive` (required)

```json
{ "archived": true }
```

| Property | Type | Required |
| --- | --- | --- |
| `archived` | boolean | Yes |

### Responses

- **200** — `AccountInformation`

- **404** — Account not found.

## Schemas

**`AccountInformation`** — `accountId: uuid`, `name: string`, `description: string`, `currentBalance: number`, `currency: string`, `institutionName: string`, `accountNumberMasked: string`, `active: boolean`.

**`AccountListResponse`** — `entityList: AccountInformation[]`, `message: string`.

**Error response** — `status: integer`, `errors: [{ clue?: string, field?: string, message: string }]`.