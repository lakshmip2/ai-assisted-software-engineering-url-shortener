# Risk Register

## Objective

Identify potential technical and engineering risks associated with the implementation and define mitigation strategies.

---

# Risk Assessment

| ID | Risk | Impact | Probability | Mitigation |
|----|------|--------|-------------|------------|
| R1 | Short code collision | High | Low | Check uniqueness before persistence |
| R2 | Invalid URL input | Medium | Medium | Bean Validation + business validation |
| R3 | Expired URL access | Medium | Medium | Expiration validation before redirect |
| R4 | Concurrent click updates may cause optimistic-lock conflicts. | Medium | Medium | JPA @Version detects conflicting updates. |
| R5 | Database unavailability | High | Low | Health checks and retry strategy (future) |
| R6 | Predictable short codes | High | Low | SecureRandom generator |
| R7 | Performance degradation | Medium | Low | Introduce Redis caching (future) |
| R8 | Lack of observability | Medium | Medium | Add structured logging and metrics |

---

# Technical Debt

The following improvements were intentionally deferred:

- Redis Cache
- Kafka Event Publishing
- Rate Limiting
- User Authentication
- Multi-region Deployment
- Distributed Locking
- Prometheus Monitoring
- Grafana Dashboards
- Use atomic database increment or an event-based analytics counter.

---

# Residual Risks

These risks remain acceptable for the prototype:

- Single instance deployment
- Local database
- No authentication
- No caching
- The prototype does not retry failed concurrent updates.

These would be addressed before production deployment.