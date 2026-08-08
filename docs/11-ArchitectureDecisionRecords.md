# Architecture Decision Records

## ADR-001

### Decision

Use Layered Architecture.

### Reason

Improves separation of concerns, maintainability, and testability.

---

## ADR-002

### Decision

Use UUID as primary key.

### Reason

Prevents sequential ID enumeration.

---

## ADR-003

### Decision

Use SecureRandom for short code generation.

### Reason

Improves randomness and reduces predictability.

---

## ADR-004

### Decision

Use Soft Delete.

### Reason

Preserves analytics and audit history.

---

## ADR-005

### Decision

Use Spring Boot.

### Reason

Enterprise standard with excellent ecosystem support.

---

## ADR-006

### Decision

Use PostgreSQL for production.

### Reason

Reliable ACID-compliant relational database.

---

## ADR-007

### Decision

Use H2 for local development.

### Reason

Fast setup and simplified testing.

---

## ADR-008

### Decision

Engineer-led AI workflow.

### Reason

Maintain engineering ownership while leveraging AI for productivity.