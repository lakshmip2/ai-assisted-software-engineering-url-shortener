# AI-Assisted Software Engineering – URL Shortener

A production-oriented URL Shortener implemented using **Java, Spring Boot, Spring Data JPA, and PostgreSQL**, with a strong focus on **AI-assisted software engineering, clean architecture, testing, security, documentation, and production scalability**.

This project was developed as part of the **AI Engineering Assessment** for the **Sr Manager Software Development and Engineering** opportunity.

---

## 1. Executive Summary

This project demonstrates the design and implementation of a URL Shortening service capable of:

* Creating shortened URLs
* Supporting custom aliases
* Redirecting users through short URLs
* Managing URL expiration
* Handling duplicate aliases
* Supporting soft deletion
* Providing analytics
* Validating API requests
* Handling application errors consistently
* Providing automated tests
* Using AI-assisted engineering throughout the development lifecycle

The implementation intentionally uses a **modular Spring Boot architecture** rather than introducing distributed infrastructure that is not required to validate the assessment requirements.

At the same time, the architecture has been designed with a clear evolution path toward a production-scale platform using components such as:

* API Gateway
* Redis
* Kafka
* Horizontal application scaling
* PostgreSQL read replicas
* Rate limiting
* Kubernetes
* Observability and monitoring

These production components are documented as an architectural evolution path rather than being added solely for technology complexity.

---

# 2. Architecture at a Glance

## Current Assessment Architecture

```text
                    Client
                       |
                       v
                REST API Layer
                       |
                       v
              Spring Boot Application
                       |
          +------------+------------+
          |                         |
          v                         v
   URL Shortener              Analytics
      Service                  Service
          |                         |
          +------------+------------+
                       |
                       v
                Spring Data JPA
                       |
                       v
                   PostgreSQL
```

The current implementation focuses on functional correctness, maintainability, testability, and clear separation of responsibilities.

## Production Evolution

```text
                         Clients
                            |
                            v
                 Load Balancer / API Gateway
                            |
              +-------------+-------------+
              |             |             |
              v             v             v
          Spring Boot   Spring Boot   Spring Boot
           Instance 1    Instance 2     Instance N
              |             |             |
              +-------------+-------------+
                            |
                    +-------+-------+
                    |               |
                    v               v
                  Redis         PostgreSQL
                  Cache        Primary/Replica
                    |
                    |
             Redirect Events
                    |
                    v
                  Kafka
                    |
                    v
            Analytics Consumers
                    |
                    v
             Analytics Store
```

The production architecture is an evolution path based on workload, availability, security, and scalability requirements.

Detailed architecture documentation is available in:

`docs/03-Architecture.md`

---

# 3. Why the Current Implementation Is Intentionally Simple

A key architectural decision was to avoid introducing distributed infrastructure without a demonstrated requirement.

For example, Kafka, Redis, Kubernetes, and an API Gateway could all be added to a URL shortener. However, introducing them into a small assessment implementation would increase:

* operational complexity
* infrastructure dependencies
* failure modes
* deployment complexity
* testing requirements
* maintenance cost

The current implementation therefore focuses on the core business capability while documenting how the system can evolve when production workload requires additional infrastructure.

This follows the principle:

> **Introduce architectural complexity when it solves a measurable problem, not simply because the technology is available.**

---

# 4. Scalability Strategy

The architecture is designed to evolve from a single application instance to a horizontally scalable service.

Example production assumptions:

* 10 million stored URLs
* 100 million redirects/day
* Approximately 1,157 average redirects/second
* Peak traffic potentially several times higher

At higher traffic volumes:

### Redis

Can be introduced for frequently accessed URL mappings to reduce database reads and redirect latency.

### Horizontal Scaling

Multiple Spring Boot instances can be deployed behind a load balancer/API Gateway.

### Kafka

Redirect events can be published asynchronously so analytics processing does not increase the latency of the redirect path.

### PostgreSQL

Indexing, connection pooling, read replicas, and high-availability configurations can be introduced according to workload requirements.

Detailed scalability analysis:

`docs/17-ScalabilityAndPerformance.md`

---

# 5. AI-Assisted Engineering

AI was used as an engineering accelerator throughout the development lifecycle rather than as a replacement for engineering judgment.

The workflow included:

```text
Requirements
     |
     v
AI-Assisted Analysis
     |
     v
Architecture Alternatives
     |
     v
Engineer Decision
     |
     v
Implementation
     |
     v
AI-Assisted Review
     |
     v
Testing & Validation
     |
     v
Human Verification
```

AI assistance was used for activities including:

* Requirement analysis
* Edge-case identification
* Architecture exploration
* Code generation assistance
* Unit-test generation
* Test-case expansion
* Documentation
* Code review assistance
* Security review assistance
* Refactoring suggestions

All generated or suggested changes were reviewed and validated by the developer.

AI prompts and traceability artifacts are maintained in the repository.

---

# 6. Engineering Quality

The project emphasizes:

* Clean separation of responsibilities
* Input validation
* Exception handling
* Secure coding practices
* Unit testing
* Integration testing
* API validation
* Database validation
* Documentation
* CI validation
* Maintainable Java/Spring Boot code

The goal was not simply to make the application work, but to demonstrate an engineering process that can be applied to production software.

---

# 7. Security Considerations

Security considerations include:

* Input validation
* URL validation
* API boundary validation
* Error handling without unnecessary internal information exposure
* Dependency management
* Secure configuration practices
* Rate-limiting considerations
* Authentication/authorization as a production evolution capability

For enterprise deployment, the architecture can be extended with:

* OAuth2 / OpenID Connect
* JWT
* API Gateway security policies
* Distributed rate limiting
* Secret management
* Audit logging
* Security monitoring

---

# 8. Testing Strategy

Testing covers multiple levels:

```text
Unit Tests
    |
    v
Service Tests
    |
    v
Repository / Persistence Tests
    |
    v
Integration Tests
    |
    v
API Validation
```

Testing focuses on:

* Happy paths
* Invalid inputs
* Duplicate aliases
* Expired URLs
* Missing resources
* Boundary conditions
* Exception scenarios
* Persistence behavior

Test results and validation artifacts are maintained in the repository.

---

# 9. Production Readiness Roadmap

The following roadmap illustrates how the assessment implementation could evolve into a production platform.

| Capability           | Current     | Production Evolution           |
| -------------------- | ----------- | ------------------------------ |
| Spring Boot REST API | Implemented | Horizontally scaled            |
| PostgreSQL           | Implemented | HA / read replicas             |
| Analytics            | Implemented | Event-driven processing        |
| Redis                | Future      | High-volume caching            |
| Kafka                | Future      | Asynchronous events            |
| API Gateway          | Future      | Routing/security/rate limiting |
| Authentication       | Future      | OAuth2/OIDC/JWT                |
| Kubernetes           | Future      | Container orchestration        |
| Observability        | Basic       | Metrics/logs/tracing           |
| CI/CD                | Implemented | Enterprise pipeline            |
| Rate Limiting        | Considered  | Distributed enforcement        |

---

# 10. Architectural Decision Philosophy

The project demonstrates a deliberate approach to architecture:

1. Start with the simplest architecture that satisfies the requirements.
2. Identify measurable scalability and reliability constraints.
3. Introduce infrastructure when those constraints justify it.
4. Keep services independently testable.
5. Prefer asynchronous processing where synchronous processing would increase latency.
6. Separate the critical redirect path from analytics processing.
7. Scale horizontally before unnecessarily increasing application complexity.
8. Use caching where read patterns justify it.
9. Apply security and observability according to deployment risk.

This approach balances **simplicity, maintainability, scalability, reliability, and operational cost**.

---

# 11. Repository Structure

```text
ai-assisted-software-engineering-url-shortener/
│
├── src/
│   ├── main/
│   │   └── java/
│   └── test/
│
├── docs/
│   ├── 03-SystemArchitecture.md
│   ├── 17-ScalabilityAndPerformance.md
│   ├── Architecture Decision Records
│   └── Assessment Documentation
│
├── prompts/
│   └── AI Engineering Prompts
│
├── README.md
├── pom.xml
└── ...
```

---

# 12. Key Engineering Takeaway

The primary objective of this project is not to demonstrate how many technologies can be added to a URL shortener.

It is to demonstrate the ability to:

* understand requirements
* make appropriate architectural decisions
* use AI responsibly
* implement maintainable software
* validate the implementation
* identify scalability constraints
* reason about production architecture
* communicate architectural trade-offs
* evolve the system based on measurable requirements



# Technology Stack

| Technology | Version |
|------------|----------|
| Java | 21 |
| Spring Boot | 3.5.x |
| Maven | Latest |
| PostgreSQL | 16 |
| H2 | Testing |
| Spring Data JPA | Latest |
| Spring Validation | Latest |
| OpenAPI / Swagger | Latest |
| JUnit 5 | Latest |
| Mockito | Latest |
| GitHub Actions | CI |


# Build Instructions

```bash
mvn clean install
```

Run

```bash
mvn spring-boot:run
```

Swagger

```
http://localhost:8080/swagger-ui.html
```

H2 Console

```
http://localhost:8080/h2-console
```

---

# AI Traceability

AI prompts used during implementation are available under:

```
prompts/
```

Engineering decisions and modifications are documented under:

```
docs/
```

---

# Author

Lakshmi Prasanna

AI-Assisted Software Engineering Assessment

Charles Schwab
