                       ┌──────────────────────┐
                       │       Client         │
                       └──────────┬───────────┘
                                  │
                                  ▼
                    ┌─────────────────────────┐
                    │ Spring Boot REST API    │
                    │                         │
                    │ URL Controller          │
                    └────────────┬────────────┘
                                 │
                                 ▼
                    ┌─────────────────────────┐
                    │ URL Shortener Service   │
                    │                         │
                    │ Validation              │
                    │ Alias handling          │
                    │ Expiration              │
                    │ Analytics               │
                    │ Soft delete             │
                    └────────────┬────────────┘
                                 │
                ┌────────────────┴────────────────┐
                │                                 │
                ▼                                 ▼
       ┌─────────────────┐              ┌─────────────────┐
       │ ShortCode       │              │ JPA Repository  │
       │ Generator       │              └────────┬────────┘
       └─────────────────┘                       │
                                                 ▼
                                       ┌─────────────────┐
                                       │ PostgreSQL      │
                                       │ H2 for tests    │
                                       └─────────────────┘

Cross-cutting:
├── Validation
├── Global Exception Handling
├── JPA Auditing
├── OpenAPI
└── Actuator


# Prompt

Design a production-quality URL Shortener Service.

Constraints

- Spring Boot

- PostgreSQL

- Layered Architecture

- Clean Code

- DTO

- Exception Handling

Acceptance Criteria

- REST APIs

- Validation

- Unit Tests

- Production Ready

Engineer Review

The generated architecture was reviewed.

Changes included:

- Soft Delete

- Optimistic Locking

- DTO Separation

- Global Exception Handling