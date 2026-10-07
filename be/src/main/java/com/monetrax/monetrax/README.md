# Monetrax Backend

Spring Boot backend source lives in this directory. The Java application is rooted at `src/main/java/com/monetrax/monetrax` and organized by business capability. Each capability package owns its controller, request/response DTOs, service layer, persistence components, and—in applicable modules—entities, mappers, and exceptions.

## API service guides

| Service package | Responsibility | API guide |
| --- | --- | --- |
| `accounts` | Financial accounts, balances, archive state | [Accounts README](src/main/java/com/monetrax/monetrax/accounts/README.md) |
| `ai` | User-managed provider API keys | [AI keys README](src/main/java/com/monetrax/monetrax/ai/README.md) |
| `alerts` | Account alerts and category conditions | [Alerts README](src/main/java/com/monetrax/monetrax/alerts/README.md) |
| `analytics` | Aggregated transaction analytics | [Analytics README](src/main/java/com/monetrax/monetrax/analytics/README.md) |
| `auth` | Login, JWT refresh/logout, health check | [Authentication README](src/main/java/com/monetrax/monetrax/auth/README.md) |
| `categories` | User transaction categories | [Categories README](src/main/java/com/monetrax/monetrax/categories/README.md) |
| `transactions` | Transactions and related details, recurrence, AI-assisted creation | [Transactions README](src/main/java/com/monetrax/monetrax/transactions/README.md) |
| `user` | Registration, profile, password | [User README](src/main/java/com/monetrax/monetrax/user/README.md) |

## Package structure

```
com.monetrax.monetrax/
├── MonetraxApplication.java       # Spring Boot entry point
├── accounts/                      # Account controller, DTOs, service, repository, entity
├── ai/                            # Provider key management and encrypted key persistence
├── alerts/                        # Alert rules, condition DTOs, repositories, scheduler-facing service
├── analytics/                     # Analytics request/response and aggregation service
├── auth/                          # Login, JWT filter/services, refresh token persistence
├── categories/                    # Category API, entity, repository, mapper
├── common/                        # Shared exceptions, validation annotations/validators
├── config/                        # Spring Security, CORS, security error handlers
├── transactions/                  # Transaction API, entities, sub-resource services, recurrence scheduler
└── user/                          # User API, entity, repository, mapper, service
```

Within service packages, the common roles are:

- `controller/`: HTTP routes and status/body mapping.

- `dto/`: API request and response shapes; these are the public JSON contract.

- `service/` and `service/impl/`: application/business logic.

- `repository/`: Spring Data persistence access.

- `entity/`: JPA database models.

- `mapper/`: entity/DTO conversions.

- `exceptions/` or `exception/`: service-specific error types.

Not every module uses every subpackage. For example, analytics is largely a controller/DTO/service module; shared validation and error handling live outside the business modules.

## Request and security flow

1. Spring MVC routes a request to the module controller.

1. JSON request bodies are deserialized into DTOs and DTO constraints run where `@Valid` is present.

1. For authenticated routes, `JwtAuthFilter` validates the bearer JWT and makes the authenticated user available as `CustomUserDetails`.

1. The controller delegates work to a service; services use repositories and mappers to read/write entities.

1. Controllers return DTOs (the service guides identify each route's response type).

Most routes require `Authorization: Bearer <authToken>`. The unauthenticated exceptions are account creation (`POST /user/create`), authentication routes under `/auth`, and the auth health check. Treat all UUID path parameters as UUIDs. The API paths below are relative to the server base URL; no additional global API prefix is declared in the controllers.

Login and refresh place the refresh token in an HttpOnly, Secure, SameSite=Strict cookie named `refreshToken`, scoped to `/auth/token`; clients must send that cookie to refresh/logout. The access token is returned in the JSON `authToken` field.

## Shared errors

`common/exception/GlobalExceptionHandler.java` maps domain, authentication, and request-validation errors to a shared JSON shape:

```json
{
  "status": 400,
  "errors": [
    { "clue": "fieldName", "message": "Validation message" }
  ]
}
```

The `errors` entries may contain `clue`, `field`, and/or `message` depending on the exception handler. Common outcomes include 400 for validation/malformed requests, 401 for authentication failures, 403 for selected business conflicts, and 404 for missing resources. Endpoint success responses are generally HTTP 200, as returned by the controllers.

## Persistence and runtime configuration

- Maven project/build: `pom.xml`; Maven wrapper: `mvnw`.

- Spring configuration: `src/main/resources/application.yaml` .

- Flyway migration scripts: `src/main/resources/db/migration/` for the standard database and `src/main/resources/db-migration-h2/` for H2. The migrations establish user, category, account, transaction, alert, scheduler, refresh-token, and AI-key tables.

- `transactions/schedule/TransactionScheduler.java` handles scheduled recurrence processing.

- `config/` configures CORS and Spring Security; `common/validaton/` (package spelling as in source) contains shared custom bean validation.

