Task:
Implement URL creation service.

Intent:
Create a production-quality URL mapping with validation.

Constraints:
- Spring Boot
- PostgreSQL
- JPA
- No business logic in controller
- Proper exception handling

Acceptance criteria:
- Original URL must be validated
- Custom alias must be unique
- Generated aliases must be collision-safe
- Appropriate HTTP errors must be returned
- Unit tests must cover success and failure paths