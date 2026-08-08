# System Architecture

## High-Level Architecture

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


Request → Controller → Service → Repository → Database → Response

and

Redirect → lookup → validation → click increment → Location header

Cross-cutting:
├── Validation
├── Global Exception Handling
├── JPA Auditing
├── OpenAPI
└── Actuator

---

# Components

## Controller

Responsible for HTTP request handling.

## Service

Contains business rules.

## Repository

Handles persistence.

## Utility

Short code generation.

## DTO

API contracts.

## Exception

Centralized error handling.

---

# Design Principles

- Separation of Concerns

- SOLID

- Layered Architecture

- Dependency Injection

- Stateless Services

---

# Future Enhancements

- Redis

- Kafka

- Kubernetes

- Prometheus

- Grafana

- Elastic

- Event Sourcing