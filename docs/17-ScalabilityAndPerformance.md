# 14 - Scalability and Performance

## 1. Purpose

This document describes the scalability and performance considerations for the URL Shortener.

The current assessment implementation is intentionally designed as a modular Spring Boot application backed by PostgreSQL. The purpose of this document is to demonstrate how the system can evolve from the assessment implementation into a production-scale service as traffic, data volume, availability, and operational requirements increase.

The production architecture is based on measurable workload requirements rather than introducing distributed infrastructure solely for technology complexity.

---

# 2. Performance Characteristics of a URL Shortener

A URL-shortening service typically has two major traffic patterns:

1. URL creation and management
2. URL redirection

The redirect operation is expected to be significantly more frequent than URL creation.

For example:

```text
URL Creation
    |
    | Lower frequency
    v
+-------------------+
| Create Short URL  |
+-------------------+


URL Redirect
    |
    | High frequency
    v
+-------------------+
| GET /{shortCode}  |
+-------------------+
```

Therefore, the redirect path should be optimized primarily for:

* Low latency
* High throughput
* High availability
* Efficient cache utilization
* Minimal database dependency

Analytics should also avoid unnecessarily increasing the latency of the redirect operation.

---

# 3. Baseline Architecture

The current implementation uses:

```text
Client
  |
  v
REST API
  |
  v
Spring Boot
  |
  +-------------------+
  |                   |
  v                   v
URL Service       Analytics
  |                   |
  +---------+---------+
            |
            v
       Spring Data JPA
            |
            v
        PostgreSQL
```

This architecture is appropriate for the assessment because it provides:

* Simple deployment
* Low operational complexity
* Strong transactional consistency
* Easy local development
* Straightforward testing
* Clear separation of responsibilities
* Minimal infrastructure dependencies

---

# 4. Scalability Assumptions

The following workload is used as an example for architectural capacity planning.

These values are **planning assumptions and not measured results from the current implementation**.

### Example workload

* 10 million stored URLs
* 100 million redirects per day
* Redirect traffic is significantly higher than URL creation traffic
* Peak traffic may reach approximately 10 times the daily average
* High availability is required for the redirect path

---

# 5. Request Rate Estimation

Assuming 100 million redirects per day:

```text
100,000,000 redirects
---------------------
      86,400 seconds

≈ 1,157 redirects/second
```

Therefore:

```text
Average throughput ≈ 1,157 requests/second
```

If peak traffic is approximately 10 times the average:

```text
1,157 × 10
≈ 11,570 requests/second
```

This provides an example peak planning target of approximately:

**11.5K redirects/second**

This number is an architectural planning assumption and must be validated through load testing before making production capacity commitments.

---

# 6. Read / Write Characteristics

A URL shortener is generally read-heavy.

A simplified workload model could be:

```text
                 Traffic
                    |
          +---------+---------+
          |                   |
          v                   v
      Redirects          URL Management
       ~95%                 ~5%
```

The exact ratio depends on business usage.

The important architectural implication is that the redirect path should be optimized for high-volume reads while URL creation and management can remain strongly consistent.

---

# 7. Horizontal Scaling

The Spring Boot application should be designed to support multiple application instances.

Instead of:

```text
Client
  |
  v
Spring Boot Instance
  |
  v
PostgreSQL
```

the production architecture can evolve to:

```text
                         Client
                           |
                           v
                    Load Balancer
                           |
             +-------------+-------------+
             |             |             |
             v             v             v
         Instance 1    Instance 2    Instance N
             |             |             |
             +-------------+-------------+
                           |
                           v
                       Database
```

## Benefits

Horizontal scaling provides:

* Increased throughput
* Better availability
* Fault isolation
* Rolling deployments
* Independent application scaling

The application should remain stateless where practical so that requests can be distributed between instances.

---

# 8. Stateless Application Design

Application instances should avoid storing request-specific state locally.

For example:

```text
Request
   |
   v
Instance 1
   |
   X
Local session state
```

should be avoided where possible.

Instead:

```text
                  +----------------+
                  | Shared Storage |
                  | DB / Redis     |
                  +-------+--------+
                          |
          +---------------+---------------+
          |               |               |
          v               v               v
      Instance 1      Instance 2      Instance N
```

This allows any instance to process a request.

Statelessness simplifies:

* Horizontal scaling
* Failover
* Rolling deployment
* Container orchestration
* Load balancing

---

# 9. Redis Caching Strategy

The redirect operation is an excellent candidate for caching because short-code-to-original-URL mappings are frequently read.

A production cache architecture can be:

```text
Client
  |
  v
Redirect API
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
        Redis Update
            |
            v
       Original URL
```

## Cache Key

A possible cache key is:

```text
url:{shortCode}
```

Example:

```text
url:abc123
```

## Cached Value

The cached value can contain:

* Original URL
* Expiration information
* Status where appropriate

---

# 10. Cache Benefits

Caching can reduce:

* Database read volume
* Redirect latency
* Database connection pressure
* CPU consumed by repeated lookups

It can also increase:

* Redirect throughput
* Application scalability
* Resilience during database read pressure

---

# 11. Cache Invalidation

Caching introduces an important consistency problem.

If a URL is:

* Deleted
* Modified
* Expired
* Disabled

the corresponding cache entry must not remain valid indefinitely.

Possible strategies include:

### Write-through/update

Update or invalidate the cache when the underlying URL changes.

### TTL

Assign a time-to-live to cached entries.

### Explicit invalidation

Delete the cache entry after a URL modification or deletion.

Example:

```text
Update URL
    |
    v
PostgreSQL Update
    |
    v
Redis Invalidation
```

The exact strategy should depend on the required consistency model.

---

# 12. Database Performance

PostgreSQL remains the system of record.

Database performance should first be improved through conventional optimization before introducing additional infrastructure.

Key areas include:

* Proper indexing
* Query optimization
* Connection pooling
* Transaction management
* Appropriate schema design
* Avoiding unnecessary queries
* Pagination for large result sets
* Monitoring slow queries

---

# 13. Database Indexing

The short-code lookup is one of the most important database operations.

A lookup such as:

```text
GET /abc123
```

requires an efficient lookup of:

```text
shortCode = "abc123"
```

The `shortCode` column should therefore have an appropriate index and uniqueness constraint.

Conceptually:

```text
Client
  |
  v
shortCode
  |
  v
Indexed lookup
  |
  v
URL record
```

This prevents an inefficient full-table scan as the number of stored URLs increases.

---

# 14. Database Connection Pooling

A production deployment with multiple application instances should use controlled database connection pooling.

Conceptually:

```text
Instance 1 ----+
Instance 2 ----+----> Connection Pool ----> PostgreSQL
Instance 3 ----+
```

Connection pooling reduces the overhead of repeatedly creating database connections.

However, the total number of connections must be controlled.

For example:

```text
Application Instances × Pool Size
```

must remain within the database's capacity.

Increasing the number of application instances without considering database connection limits can move the bottleneck from the application layer to PostgreSQL.

---

# 15. Read Replicas

If database read traffic becomes a bottleneck, PostgreSQL read replicas can be considered.

Possible topology:

```text
                  PostgreSQL Primary
                         |
             +-----------+-----------+
             |                       |
             v                       v
       Read Replica 1          Read Replica 2
```

Potential use:

* Writes → Primary
* Read-heavy operations → Replicas

However, read replicas introduce replication lag.

Therefore, operations requiring strong read-after-write consistency may still need to access the primary database.

---

# 16. Redirect Path Optimization

The redirect path should be kept as short as possible.

A production request flow can be:

```text
Client
  |
  v
API Gateway / Load Balancer
  |
  v
Spring Boot
  |
  v
Redis
  |
  +---- Hit ----> Redirect Response
  |
  +---- Miss
          |
          v
      PostgreSQL
          |
          v
        Redis
          |
          v
      Redirect
```

The goal is to prevent non-critical operations from delaying the redirect response.

---

# 17. Asynchronous Analytics

Analytics processing should not unnecessarily block the redirect response.

A synchronous design can create this dependency:

```text
Redirect
   |
   v
Analytics Processing
   |
   v
Database Update
   |
   v
Response
```

This means analytics latency can directly affect redirect latency.

A production design can decouple the two:

```text
                     +----> Redirect Response
                     |
Redirect Service ----+
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
              Analytics Store
```

This allows analytics processing to occur asynchronously.

---

# 18. Why Kafka Is Not Required in the Current Implementation

Kafka is not automatically required simply because the system has analytics.

The current assessment implementation can remain synchronous because:

* The application scope is limited.
* Operational simplicity is valuable.
* The assessment does not require distributed event processing.
* The additional infrastructure would increase deployment and testing complexity.

Kafka becomes more appropriate when:

* Event volume becomes significant.
* Analytics processing needs independent scaling.
* Multiple consumers need redirect events.
* Asynchronous processing is required.
* Event durability and replay become important.

This is an example of an architectural trade-off rather than a missing technology.

---

# 19. API Gateway and Rate Limiting

At production scale, an API Gateway can provide centralized traffic management.

Responsibilities may include:

* Authentication
* Authorization
* Request routing
* API versioning
* Rate limiting
* Request throttling
* TLS termination
* Traffic policies

Example:

```text
Client
  |
  v
API Gateway
  |
  +---- Rate Limit
  |
  +---- Authentication
  |
  v
Spring Boot Instances
```

Distributed rate limiting may use Redis or another shared mechanism when multiple application instances are deployed.

---

# 20. Abuse Protection

A public URL-shortening service can be vulnerable to abuse.

Potential risks include:

* Automated URL creation
* Excessive redirect requests
* Malicious URL submission
* Resource exhaustion
* Analytics endpoint abuse

Possible controls include:

* Rate limiting
* Authentication for administrative APIs
* Request validation
* URL validation
* Abuse detection
* IP/client throttling
* Monitoring and alerting

These controls should be implemented according to the deployment environment and threat model.

---

# 21. Resilience

Production architecture should consider failures in:

* Application instances
* PostgreSQL
* Redis
* Kafka
* Network communication
* External dependencies

The application should fail gracefully where possible.

Example:

```text
Redis Failure
     |
     v
Fallback to PostgreSQL
     |
     v
Continue Redirect
```

Redis should therefore be treated as a performance optimization rather than the only source of truth.

Similarly:

```text
Kafka Failure
     |
     v
Analytics degradation
```

should not necessarily cause the critical redirect path to fail.

The exact behavior depends on the required reliability and consistency guarantees.

---

# 22. Timeout and Retry Strategy

Retries should be used carefully.

Blind retries can amplify failures.

For example:

```text
Application
    |
    v
Database
    X
Failure
    |
    v
Retry
    |
    X
Failure
    |
    v
Retry
```

Large retry volumes can create a cascading failure.

Production implementations should use:

* Bounded retries
* Exponential backoff
* Timeouts
* Circuit breakers where appropriate
* Idempotency for retryable operations

---

# 23. Observability

Scalability cannot be managed effectively without measurement.

Important metrics include:

### Application

* Requests per second
* Response latency
* Error rate
* HTTP status distribution
* JVM CPU
* JVM memory

### Database

* Query latency
* Connection utilization
* Slow queries
* CPU
* Storage
* Replication lag

### Redis

* Cache hit ratio
* Cache miss ratio
* Memory utilization
* Eviction rate
* Command latency

### Kafka

* Consumer lag
* Producer errors
* Throughput
* Partition utilization

---

# 24. Key Performance Indicators

A production system should establish measurable targets.

Examples:

```text
Request Rate
Latency
Error Rate
Cache Hit Ratio
Database Utilization
CPU Utilization
Memory Utilization
```

For example:

```text
Cache Hit Ratio

Cache Hits
-----------------------------
Cache Hits + Cache Misses
```

A low cache hit ratio may indicate:

* Poor cache key design
* Insufficient cache capacity
* Low URL locality
* Short TTL
* High URL churn

---

# 25. Load Testing

The scalability assumptions should be validated through load testing.

A possible test progression is:

```text
100 req/s
    |
    v
500 req/s
    |
    v
1,000 req/s
    |
    v
5,000 req/s
    |
    v
10,000+ req/s
```

The actual test targets should depend on the expected production workload.

Load testing should measure:

* Average latency
* p95 latency
* p99 latency
* Throughput
* Error rate
* CPU utilization
* Memory utilization
* Database utilization
* Cache hit ratio

---

# 26. Performance Test Scenarios

Recommended scenarios include:

### Scenario 1 – Normal Redirect

```text
GET /{shortCode}
```

Measure:

* Response latency
* Database access
* Cache behavior

### Scenario 2 – Cache Hit

```text
GET /{frequently-used-shortCode}
```

Measure:

* Redis latency
* End-to-end latency
* Database bypass rate

### Scenario 3 – Cache Miss

Measure:

* Redis miss
* PostgreSQL lookup
* Cache population
* End-to-end latency

### Scenario 4 – High Concurrent Redirects

Simulate thousands of concurrent redirect requests.

Measure:

* Throughput
* p95/p99 latency
* Error rate
* Resource utilization

### Scenario 5 – Analytics Load

Generate high redirect volumes while analytics processing occurs.

Verify that:

```text
Analytics load
      |
      X
      |
Redirect latency
```

does not degrade beyond acceptable limits.

---

# 27. Bottleneck Analysis

Potential bottlenecks include:

```text
Client
  |
  v
API Gateway
  |
  v
Application
  |
  +---- CPU
  |
  +---- Memory
  |
  v
Redis
  |
  v
PostgreSQL
  |
  +---- CPU
  +---- Connections
  +---- I/O
  +---- Query latency
```

The architecture should be scaled based on the actual bottleneck.

For example:

| Bottleneck           | Potential Action                 |
| -------------------- | -------------------------------- |
| Application CPU      | Add application instances        |
| Application memory   | Tune JVM / scale instances       |
| High DB reads        | Redis / read replicas            |
| Slow DB queries      | Index/query optimization         |
| DB connections       | Tune pooling / database capacity |
| Analytics processing | Kafka + independent consumers    |
| API abuse            | Rate limiting                    |
| Gateway saturation   | Scale gateway infrastructure     |

---

# 28. Capacity Planning

Capacity planning should consider:

```text
Traffic
+
Data Volume
+
Concurrency
+
Latency Requirements
+
Availability Requirements
```

For example:

```text
10M URLs
+
100M redirects/day
+
10x peak assumption
+
High availability
```

may justify:

* Multiple Spring Boot instances
* Redis caching
* Database optimization
* Read replicas
* Asynchronous analytics

However, the final architecture should be determined using measured workload data.

---

# 29. Scaling Decision Framework

The following decision framework can be used:

```text
                Performance Problem
                        |
                        v
                Measure Bottleneck
                        |
              +---------+---------+
              |                   |
              v                   v
          Application           Database
              |                   |
              v                   v
        Horizontal Scale    Query / Index Optimization
                                  |
                                  v
                             Still High?
                                  |
                                  v
                             Redis / Replica
                                  |
                                  v
                         Analytics Bottleneck?
                                  |
                                  v
                              Kafka
```

This avoids introducing infrastructure without understanding the actual bottleneck.

---

# 30. Production Evolution Roadmap

## Phase 1 – Assessment Implementation

```text
Spring Boot
+
PostgreSQL
+
REST API
+
Testing
```

Focus:

* Functional correctness
* Maintainability
* Validation
* Documentation

---

## Phase 2 – Production Hardening

```text
Load Balancer
+
Multiple Spring Boot Instances
+
Monitoring
+
Rate Limiting
+
Security
```

Focus:

* Availability
* Security
* Observability
* Horizontal scalability

---

## Phase 3 – High-Read Optimization

```text
Redis
+
Database Optimization
+
Read Replicas
```

Focus:

* Redirect latency
* Database load
* Throughput

---

## Phase 4 – Event-Driven Analytics

```text
Redirect Service
       |
       v
     Kafka
       |
       v
Analytics Consumers
```

Focus:

* Analytics scalability
* Decoupling
* Independent processing

---

## Phase 5 – Enterprise Platform

```text
API Gateway
+
Kubernetes
+
Autoscaling
+
Distributed Observability
+
Security Platform
+
Resilient Data Architecture
```

Focus:

* Enterprise availability
* Automated scaling
* Operational maturity
* Governance
* Resilience

---

# 31. Architecture Trade-Offs

| Decision               | Benefit                                     | Trade-Off                                     |
| ---------------------- | ------------------------------------------- | --------------------------------------------- |
| PostgreSQL             | Strong consistency and simplicity           | Database scaling required at high traffic     |
| Redis                  | Low-latency reads                           | Cache invalidation and operational dependency |
| Kafka                  | Decoupled asynchronous processing           | Operational complexity                        |
| API Gateway            | Centralized security and traffic management | Additional network hop and infrastructure     |
| Horizontal scaling     | Higher throughput and availability          | Requires stateless design                     |
| Read replicas          | Increased read capacity                     | Replication lag                               |
| Kubernetes             | Automated deployment/scaling                | Operational complexity                        |
| Synchronous analytics  | Simple implementation                       | Can increase redirect latency                 |
| Asynchronous analytics | Better separation and scalability           | Eventual consistency                          |

---

# 32. Key Architectural Principle

The system should not be made complex simply to demonstrate scalability.

A strong production architecture should answer three questions:

### 1. What is the workload?

Example:

```text
100M redirects/day
```

### 2. Where is the bottleneck?

Example:

```text
Database read capacity
```

### 3. What is the simplest technology that solves the bottleneck?

Example:

```text
Redis
```

This creates a traceable relationship:

```text
Requirement
    ↓
Workload
    ↓
Bottleneck
    ↓
Architecture Decision
    ↓
Technology
    ↓
Measured Result
```

---

# 33. Final Summary

The current URL Shortener implementation intentionally provides a simple, maintainable, and testable Spring Boot architecture.

The production scalability strategy provides a clear path toward:

* Horizontal application scaling
* Redis-based caching
* PostgreSQL optimization and replication
* API Gateway integration
* Rate limiting
* Event-driven analytics
* Kafka-based asynchronous processing
* Containerized deployment
* Kubernetes-based orchestration
* Monitoring and observability
* Resilience and fault isolation

The architecture should evolve incrementally as actual workload and business requirements increase.

The central engineering principle is:

> **Start simple, measure continuously, identify the bottleneck, and introduce complexity only when it solves a demonstrated problem.**
