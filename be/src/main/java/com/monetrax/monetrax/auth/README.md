# Authentication API

**Tag:** Authentication
**Base path:** `/auth`
**Security:** Login, refresh, logout, and health check do not require bearer access-token authentication. Refresh/logout require the refresh-token cookie where stated.

## `POST /auth/login`

**Operation:** `login`
**Summary:** Authenticate with email and password; issue access and refresh tokens.

### Request body — `AuthRequest` (required)

```json
{ "email": "user@example.com", "password": "Your-password1!" }
```

| Property | Type | Required | Constraints |
| --- | --- | --- | --- |
| `email` | string | Yes | Nonblank; valid email |
| `password` | string | Yes | Nonblank |

### Responses

- **200** — `AuthResponse` JSON and `Set-Cookie: refreshToken=...`

- **400** — Validation error.

- **401** — Invalid credentials or authentication failure.

## `POST /auth/token/refresh`

**Operation:** `refreshTokens`
**Summary:** Rotate refresh token and issue a new access JWT.

### Parameters / cookie

| Name | In | Type | Required |
| --- | --- | --- | --- |
| `refreshToken` | cookie | string | Yes |

No JSON request body.

### Responses

- **200** — `AuthResponse` JSON and refreshed `Set-Cookie` header.

- **401** — Missing/invalid/expired refresh token.

## `POST /auth/token/logout`

**Operation:** `logout`
**Summary:** Revoke the refresh token and clear its cookie.

### Parameters / cookie

| Name | In | Type | Required |
| --- | --- | --- | --- |
| `refreshToken` | cookie | string | Yes |

No JSON request body.

### Responses

- **200** — Plain-text `Successfully logged out.`; refresh cookie is cleared.

- **401** — Missing or invalid refresh token.

## `GET /auth/health-check`

**Operation:** `healthCheck`
**Summary:** Check that the auth controller is responding.

### Responses

- **200** — Plain-text `Health test: ok.`

## Schemas and authentication details

**`AuthResponse`** — `userId: string`, `authToken: string` (JWT access token).

The refresh cookie is named `refreshToken`; it is HttpOnly, Secure, SameSite=Strict, and scoped to `/auth/token`. Browser clients should send the cookie on refresh/logout requests. For protected endpoints, send `Authorization: Bearer <authToken>`.

**Error response** — `status: integer`, `errors: [{ clue?: string, field?: string, message: string }]`.