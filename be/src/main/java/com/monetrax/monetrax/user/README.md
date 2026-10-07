# User API

**Tag:** User
**Base path:** `/user`
**Security:** `POST /user/create` is public. All other operations require `BearerAuth`.
**Content type:** `application/json` for request bodies. Successful operations return `200 OK`.

## `GET /user/me`

**Operation:** `getCurrentUser`
**Summary:** Return the authenticated user's profile.

### Responses

- **200** — `UserInformation`

- **401** — Shared error response.

- **404** — User not found.

## `POST /user/create`

**Operation:** `createUser`
**Summary:** Register a user.

### Request body — `UserCreation` (required)

```json
{
  "userEmail": "user@example.com",
  "userName": "jdoe",
  "name": "Jane",
  "surname": "Doe",
  "dateOfBirth": "1990-04-12",
  "password": "Secure-pass1!"
}
```

| Property | Type | Required | Constraints |
| --- | --- | --- | --- |
| `userEmail` | string | Yes | Valid email |
| `userName` | string | Yes | 2–40 characters |
| `name` | string | Yes | 2–40 characters |
| `surname` | string | Yes | 2–40 characters |
| `dateOfBirth` | string (date) | Yes | Past date; age validator requires at least 18 |
| `password` | string | Yes | 8–40 characters; must satisfy password format validator |

### Responses

- **200** — `UserInformation`

- **400** — Validation error.

- **403** — Email already exists.

## `PATCH /user/update`

**Operation:** `updateUser`
**Summary:** Update the authenticated user's profile fields.

### Request body — `UserUpdate` (required)

At least one field must be provided. Properties are optional: `userEmail` (valid email), `userName` (2–40 chars), `name` (2–40 chars), `surname` (2–40 chars), `dateOfBirth` (past ISO date; minimum age 18).

### Responses

- **200** — `UserInformation`

- **400** — Validation error or no fields to update.

- **403** — Email already exists.

## `PATCH /user/update/password`

**Operation:** `updateUserPassword`
**Summary:** Change the authenticated user's password.

### Request body — `UserUpdatePassword` (required)

```json
{ "oldPassword": "Old-pass1!", "newPassword": "New-pass2!" }
```

| Property | Type | Required | Constraints |
| --- | --- | --- | --- |
| `oldPassword` | string | Yes | 8–40 characters; configured password format |
| `newPassword` | string | Yes | 8–40 characters; configured password format |

### Responses

- **200** — `UserSuccessfulPasswordUpdate`

- **400** — Validation error or current password mismatch.

## Schemas

**`UserInformation`** — `userId: uuid`, `userEmail: string`, `userName: string`, `name: string`, `surname: string`, `role: string`, `dateOfBirth: date`, `hasFinishedOnboarding: boolean`, `hasVerifiedEmail: boolean`, `additionalInfo: string`.

**`UserSuccessfulPasswordUpdate`** — `successfulPasswordUpdate: boolean`, `message: string`.

**Error response** — `status: integer`, `errors: [{ clue?: string, field?: string, message: string }]`.