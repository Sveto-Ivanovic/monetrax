# AI Provider Keys API

**Tag:** AI Keys
**Base path:** `/ai/keys`
**Security:** `BearerAuth` required for all operations.
**Content type:** `application/json` for request bodies. Success status: `200 OK`.

## `GET /ai/keys/status`

**Operation:** `getApiKeyStatus`
**Summary:** Return the provider key types configured for the authenticated user. Secret values are not returned.

### Parameters

None.

### Responses

- **200** — `ResponseApiKeyStatusMsg`

- **401** — Shared error response.

## `PUT /ai/keys/update`

**Operation:** `createOrUpdateApiKey`
**Summary:** Save or replace a provider API key for the authenticated user.

### Request body — `RequestApiKey` (required)

```json
{ "rawKey": "provider-secret-key", "keyType": "OPENAI_API_KEY" }
```

| Property | Type | Required | Constraints |
| --- | --- | --- | --- |
| `rawKey` | string | Yes | Minimum 4 characters; treat as a secret |
| `keyType` | `KeyType` | Yes | Enum listed below |

`KeyType` values: `GEMINI_API_KEY`, `GROQ_API_KEY`, `CLAUDE_API_KEY`, `OPENAI_API_KEY`, `MISTRAL_API_KEY`, `DEEPSEEK_API_KEY`, `OPENROUTER_API_KEY`, `COHERE_API_KEY`, `PERPLEXITY_API_KEY`, `XAI_API_KEY`.

### Responses

- **200** — `ResponseApiKeyStatusMsg`

- **400** — Validation error.

- **401** — Shared error response.

## `DELETE /ai/keys/delete/{keyType}`

**Operation:** `deleteApiKey`
**Summary:** Delete the authenticated user's saved key for the specified provider type.

### Parameters

| Name | In | Type | Required | Description |
| --- | --- | --- | --- | --- |
| `keyType` | path | `KeyType` | Yes | Provider key enum value |

### Responses

- **200** — `ResponseApiKeyStatusMsg`

- **400** — Invalid enum/path value or domain error.

- **401** — Shared error response.

## Schemas

**`ResponseApiKeyStatusMsg`** — `msg: string`, `listOfPresentKeys: string[]` (configured key types only, never raw secrets).

**Error response** — `status: integer`, `errors: [{ clue?: string, field?: string, message: string }]`.