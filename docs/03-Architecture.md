# System Architecture

## 1. Architecture Overview

The URL Shortener is implemented as a modular Spring Boot application with clear separation between the REST API, business logic, persistence, and analytics capabilities.

The current implementation intentionally focuses on the functional requirements of the assessment while maintaining a clear evolution path toward a production-scale distributed architecture.

The architecture is therefore described at two levels:

1. Current Assessment Implementation
2. Production-Scale Evolution Architecture

This distinction is intentional. Infrastructure components such as Redis, Kafka, API Gateway, Kubernetes, and distributed observability are not introduced into the assessment implementation unless they provide direct value for the current functional scope.

# 2. Current Assessment Architecture

The current implementation follows a modular layered architecture.

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


## 2.1 Components
## a. Client

The client interacts with the system through REST APIs.

Examples include:

Create short URL
Redirect using short URL
Retrieve analytics
Delete URL
Manage custom aliases
Handle URL expiration

## b. REST API Layer

The REST layer exposes HTTP endpoints for the URL-shortening functionality.

Responsibilities include:

Request handling
Request validation
Response generation
HTTP status management
API documentation through OpenAPI/Swagger

The API layer does not contain core business logic.

## c. URL Shortener Service

The URL Shortener service contains the core business functionality.

Responsibilities include:

URL creation
Short-code generation
Custom alias handling
Expiration validation
URL lookup
Redirect handling
Soft deletion
Business rule enforcement

## d. Analytics Service

The Analytics capability provides URL usage information.

Examples include:

Click count
Redirect activity
URL usage information
Analytics retrieval

Analytics is separated from the URL-management responsibilities to maintain separation of concerns and provide a clear path toward future event-driven processing.

## e. Spring Data JPA

Spring Data JPA provides persistence abstraction between the application and relational database.

Responsibilities include:

Entity persistence
Repository operations
Query execution
Transaction management
Database abstraction

## f. PostgreSQL

PostgreSQL is used as the persistent relational database.

It provides durable storage for:

URL mappings
Short codes
Original URLs
Custom aliases
Expiration information
Soft-delete information
Analytics-related data


## 3. Architectural Principles

The implementation follows the following principles:

Separation of concerns
Single responsibility
Clean layering
Maintainability
Testability
Secure coding
Validation at system boundaries
Explicit error handling
Production-oriented design
Controlled use of AI-assisted development

The implementation intentionally avoids unnecessary infrastructure complexity for the assessment scope.
---

## 4. Production-Scale Evolution Architecture

For a production environment with significantly higher traffic and availability requirements, the current application can evolve into a horizontally scalable architecture.

A possible production architecture is:

                         +------------------+
                         |     Clients      |
                         | Web / Mobile/API |
                         +--------+---------+
                                  |
                                  v
                       +----------------------+
                       | Load Balancer /      |
                       | API Gateway          |
                       +----------+-----------+
                                  |
                +-----------------+-----------------+
                |                 |                 |
                v                 v                 v
        +-------------+   +-------------+   +-------------+
        | Spring Boot |   | Spring Boot |   | Spring Boot |
        | Instance 1  |   | Instance 2  |   | Instance N  |
        +------+------+   +------+------+   +------+------+
               |                 |                 |
               +-----------------+-----------------+
                                 |
                    +------------+------------+
                    |                         |
                    v                         v
              +-----------+             +-----------+
              |   Redis   |             | PostgreSQL|
              |   Cache   |             | Primary  |
              +-----+-----+             +-----+-----+
                    |                         |
                    |                    +----+----+
                    |                    |         |
                    |                    v         v
                    |              Read Replica  Backup
                    |
                    |
                    +-----------------------------+
                                  |
                                  v
                         Redirect Response


        Redirect / Click Event
                  |
                  v
             +---------+
             |  Kafka  |
             +----+----+
                  |
          +-------+--------+
          |                |
          v                v
    Analytics Consumer   Other Consumers
        |
        v
    Analytics Storage

This architecture is an evolution path rather than part of the current assessment implementation.

## 4.1. API Gateway

An API Gateway can be introduced when the system is exposed as an enterprise-scale service.

Potential responsibilities include:

Authentication
Authorization
TLS termination
Request routing
API versioning
Rate limiting
Request throttling
Request validation
Centralized API policies

The API Gateway should not be introduced merely for architectural complexity.

It becomes valuable when the system requires centralized traffic management, security policies, multiple backend services, or external API exposure.

## 4.2 Horizontal Scalability

The Spring Boot application is designed so that application instances can be scaled horizontally.

Instead of relying on a single application instance:

    Client
    |
    v
    Spring Boot Instance

a production deployment can use:

                    Load Balancer
                         |
            +------------+------------+
            |            |            |
            v            v            v
        Instance 1   Instance 2   Instance N

Application instances should remain as stateless as practical so that requests can be distributed across instances.

This enables:

Increased throughput
Fault isolation
Rolling deployments
Better availability
Independent application scaling

## 4.3 Redis Caching Strategy

URL redirection is expected to be a read-heavy operation.

A production implementation can use Redis to cache frequently accessed short-code mappings.

Example:

    GET /abc123
        |
        v
      Redis
        |
        +---- Cache Hit ----> Original URL
        |
        +---- Cache Miss
                    |
                    v
                PostgreSQL
                    |
                    v
                Update Redis
                    |
                    v
                Original URL

Potential benefits include:

Reduced database read load
Lower redirect latency
Improved throughput
Better handling of frequently accessed URLs

Cache invalidation must be considered when URLs are deleted, modified, or expired.

## 4.4. Event-Driven Analytics

Analytics processing can be decoupled from the synchronous redirect path.

Instead of performing analytics processing synchronously:

    Client
        |
        v
    Redirect
        |
    +---- Analytics processing
|
v
Response

a production architecture can use asynchronous events:

        Client
            |
            v
        Redirect Service
            |
            +--------------------> Redirect Response
            |
            v
        Redirect Event
            |
            v
        Kafka
            |
            v
        Analytics Consumer
            |
            v
        Analytics Storage

This approach prevents analytics processing from unnecessarily increasing redirect latency.

Kafka should be introduced when event volume, independent processing, or asynchronous processing requirements justify the operational complexity.

## 4.5. Database Scalability

PostgreSQL remains the system of record.

As traffic grows, database scaling strategies can include:

Proper indexing
Query optimization
Connection pooling
Read replicas
Partitioning where appropriate
Database monitoring
Backup and recovery
High availability configuration

A possible production topology is:

                    PostgreSQL
                        |
             +----------+----------+
             |                     |
             v                     v
          Primary             Read Replica
             |
             v
          Backups

The exact topology should depend on workload, availability requirements, consistency requirements, and operational constraints.

## 4.6. Rate Limiting

A production URL-shortening service should protect the API against:

Excessive URL creation
Abuse
Automated requests
Denial-of-service patterns
Excessive analytics queries

Rate limiting can be implemented at the API Gateway or service layer.

Example:

    Client
        |
        v
    API Gateway
        |
    +---- Rate Limit Check
        |
        v
    Spring Boot Service

Distributed rate limiting may use Redis or another centralized mechanism when multiple application instances are deployed.

## 4.7. Security Architecture

For an enterprise production deployment, additional security controls can be introduced.

Potential controls include:

TLS for communication
OAuth2 / OpenID Connect
JWT validation
Role-based authorization
API rate limiting
Input validation
Secure HTTP headers
Secret management
Dependency vulnerability scanning
Audit logging
Security monitoring

Security controls should be introduced based on the application's deployment environment and threat model.

## 4.8. Observability

Production deployment should provide visibility into system health and performance.

Recommended observability areas include:

Metrics
Request rate
Redirect latency
Error rate
Database latency
Cache hit ratio
Kafka consumer lag
JVM memory
CPU utilization
Logging
Structured application logs
Error logs
Security events
Audit events
Tracing

Distributed tracing can be introduced when the architecture contains multiple services and asynchronous processing paths.

A possible monitoring architecture is:

    Application
        |
        +------ Metrics ------> Monitoring
        |
        +------ Logs ---------> Log Platform
        |
        +------ Traces -------> Tracing Platform

Prometheus and Grafana can be considered for metrics visualization and alerting.

## 4.9. Containerization and Deployment

The application can be containerized and deployed using a container orchestration platform.

Possible deployment flow:

    Developer
        |
        v
        GitHub
        |
        v
        CI Pipeline
        |
        +---- Build
        +---- Unit Tests
        +---- Integration Tests
        +---- Security Scan
        |
        v
        Container Image
        |
        v
        Container Registry
        |
        v
        Kubernetes
        |
        +---- Application Pods
        +---- Service
        +---- Horizontal Scaling
        +---- Rolling Deployment

Kubernetes is considered a production deployment option rather than a requirement of the assessment implementation.

## 4.10 Scalability Assumptions

The following example is used to reason about production scalability.

Assume:

10 million stored URLs
100 million redirects per day
Approximately 1,157 average redirects per second
Peak traffic may be several times higher than the average

Calculation:

100,000,000 redirects / 86,400 seconds
≈ 1,157 redirects/second

If peak traffic reaches approximately 10 times the average:

1,157 × 10
≈ 11,570 redirects/second

These are architectural planning assumptions rather than measured performance results.

At this scale:

Redis can reduce repeated database reads.
Multiple Spring Boot instances can handle increased application throughput.
PostgreSQL can use indexing, connection pooling, and read replicas.
Kafka can decouple analytics processing from the redirect path.
API Gateway/load balancing can distribute and control incoming traffic.

Actual capacity should be established through load testing and production workload measurements.

## 4.11 Current Architecture vs Production Architecture
    Capability	Current Assessment	Production Evolution
    REST API	Yes	Yes
    Spring Boot	Yes	Horizontally scaled
    PostgreSQL	Yes	HA / replicas as required
    Spring Data JPA	Yes	Yes
    Analytics	Synchronous/module-based	Event-driven option
    Redis	Not required	Recommended for high-read workloads
    Kafka	Not required	Recommended when event volume justifies it
    API Gateway	Not required	Recommended for enterprise API management
    Rate Limiting	Basic/application-level option	Gateway/distributed rate limiting
    Kubernetes	Not required	Production deployment option
    Observability	Basic validation/logging	Metrics, logs and tracing
    CI/CD	GitHub Actions	Enterprise CI/CD pipeline
    Authentication	Assessment scope dependent	OAuth2/OIDC/JWT
    Horizontal Scaling	Architecture-ready	Multiple instances/pods

## 4.12 Architectural Trade-Off

The primary architectural decision is to avoid over-engineering the assessment implementation.

A URL shortener can technically be implemented with many infrastructure components. However, introducing distributed infrastructure without a corresponding requirement creates additional:

Operational complexity
Failure modes
Deployment complexity
Testing requirements
Monitoring requirements
Maintenance cost

Therefore, the assessment implementation focuses on a clean and testable Spring Boot application while documenting a realistic path toward production-scale architecture.
