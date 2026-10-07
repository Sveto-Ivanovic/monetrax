# Categories API

**Tag:** Categories
**Base path:** `/categories`
**Security:** `BearerAuth` required for all operations.
**Content type:** `application/json` for request bodies. Success status: `200 OK`.

## `GET /categories/category/{category_id}`

**Operation:** `getCategory`
**Summary:** Fetch one category belonging to the authenticated user.

### Parameters

| Name | In | Type | Required |
| --- | --- | --- | --- |
| `category_id` | path | `string (uuid)` | Yes |

### Responses

- **200** — `CategoryInformation`

- **400** — Category/user identifier is invalid or not found.

- **401** — Shared error response.

## `POST /categories/create`

**Operation:** `createCategory`
**Summary:** Create a user category.

### Request body — `CategoryCreate` (required)

```json
{ "categoryType": "EXPENSE", "name": "Dining out", "description": "Restaurants and cafes" }
```

| Property | Type | Required | Constraints |
| --- | --- | --- | --- |
| `categoryType` | `CategoryKind` | Yes | Enum below |
| `name` | string | Yes | 4–50 characters |
| `description` | string | No | 6–250 characters |

`CategoryKind`: `INCOME`, `EXPENSE`, `TRANSFER_FROM`, `TRANSFER_TO`, `ADJUSTMENT_PLUS`, `ADJUSTMENT_MINUS`.

### Responses

- **200** — `CategoryInformation`

- **400** — Validation error.

- **403** — Category already exists or business conflict.

## `DELETE /categories/category/{category_id}/delete`

**Operation:** `deleteCategory`
**Summary:** Delete a category, subject to category-use/default-category rules.

### Parameters

| Name | In | Type | Required |
| --- | --- | --- | --- |
| `category_id` | path | `string (uuid)` | Yes |

### Responses

- **200** — `CategoryDeletionSuccess`

- **400** — Category/user identifier invalid or not found.

- **403** — Deletion is forbidden by a business rule.

## `PATCH /categories/category/{category_id}/update`

**Operation:** `updateCategory`
**Summary:** Change a category name and/or description.

### Parameters

| Name | In | Type | Required |
| --- | --- | --- | --- |
| `category_id` | path | `string (uuid)` | Yes |

### Request body — `CategoryUpdate` (required)

At least one field must be supplied.

| Property | Type | Constraints |
| --- | --- | --- |
| `name` | string | 4–50 characters |
| `description` | string | 6–250 characters |

### Responses

- **200** — `CategoryInformation`

- **400** — Validation error, no fields to update, or missing category.

## `GET /categories/all`

**Operation:** `getAllCategories`
**Summary:** List categories visible to the authenticated user.

### Responses

- **200** — `FetchAllCategoriesResponse`

- **401** — Shared error response.

## Schemas

**`CategoryInformation`** — `categoryId: uuid`, `categoryType: CategoryKind`, `name: string`, `description: string`, `defaultCategory: boolean`.

**`CategoryDeletionSuccess`** — `message: string`, `successfulDeletionOfCategory: boolean`.

**`FetchAllCategoriesResponse`** — `categories: CategoryInformation[]`.

**Error response** — `status: integer`, `errors: [{ clue?: string, field?: string, message: string }]`.