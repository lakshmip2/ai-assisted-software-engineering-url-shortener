# AI-Assisted Software Engineering Assessment

## Overview

This repository contains my submission for the Charles Schwab AI-Assisted Software Engineering Assessment.

The objective of this project is to demonstrate engineer-led AI-assisted software engineering by designing and implementing a production-oriented URL Shortener Service. The focus is not only on delivering a working application but also on showcasing disciplined engineering practices, architectural reasoning, AI-assisted development, validation, and engineering ownership.

---

# Assignment Objectives

The solution demonstrates:

- Requirement understanding
- Task decomposition
- Production-oriented architecture
- AI-assisted engineering workflow
- Human review and engineering ownership
- Production-quality implementation
- Validation and testing
- Risk analysis
- Brownfield engineering
- Ambiguous requirement handling
- Documentation and traceability

---

# Project Features

The application provides the following capabilities.

- Generate Short URLs
- Redirect to Original URL
- URL Analytics
- Custom Alias Support
- URL Expiration
- Soft Delete
- Input Validation
- Exception Handling
- REST APIs
- Swagger Documentation
- Unit Testing
- Integration Testing

---

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

---

# High-Level Architecture

```text
                Client
                   │
             REST API
                   │
            Spring Boot
                   │
        ---------------------
        │                   │
 URL Shortener        Analytics
        │
   Spring Data JPA
        │
    PostgreSQL
```

---

# Repository Structure

```text
.
├── architecture
├── backend
├── docs
├── prompts
├── postman
├── screenshots
├── .github
├── README.md
└── LICENSE
```

---

# Engineering Workflow

The implementation follows an engineer-led AI-assisted software engineering workflow.

```
Requirement Analysis
        ↓
Task Decomposition
        ↓
Architecture
        ↓
AI Prompt
        ↓
AI Generated Draft
        ↓
Engineer Review
        ↓
Implementation
        ↓
Testing
        ↓
Validation
        ↓
Documentation
```

The engineer owns every design decision, implementation detail, validation step, and production readiness check.

---

# AI Usage

AI was used as an engineering accelerator during the following activities.

- Requirement interpretation
- Design brainstorming
- Entity generation
- REST API generation
- Unit Test generation
- Documentation drafting
- Code review assistance
- Refactoring suggestions

All AI-generated output was reviewed, validated, modified where necessary, and approved before inclusion.

---

# Validation

The solution includes multiple validation layers.

- Input Validation
- Unit Tests
- Integration Tests
- Exception Handling
- API Validation
- Manual Functional Testing
- evidences showing swagger overview, create-url, redirect-url, analytics, delete-url, postgres db screenshots

---

# Engineering Principles

The implementation follows the following principles.

- Clean Architecture
- Separation of Concerns
- SOLID Principles
- Production Readiness
- Maintainability
- Scalability
- Testability
- Secure Coding Practices

---

# Future Enhancements

Possible future enhancements include:

- Redis Caching
- Kafka Event Publishing
- Click Analytics Dashboard
- QR Code Generation
- User Authentication
- Rate Limiting
- Distributed Deployment
- Kubernetes
- Observability using Prometheus & Grafana

---

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

# Assessment Mapping

| Assessment Requirement | Repository Artifact |
|------------------------|--------------------|
| Requirement Understanding | docs/01-RequirementAnalysis.md |
| Task Decomposition | docs/02-TaskDecomposition.md |
| Architecture | docs/03-SystemArchitecture.md |
| AI Workflow | docs/04-AIEngineeringWorkflow.md |
| AI Traceability | docs/05-AIUsageTraceability.md |
| Validation | docs/06-ValidationStrategy.md |
| Risks | docs/07-RiskRegister.md |
| Greenfield Scenario | docs/08-GreenfieldScenario.md |
| Brownfield Scenario | docs/09-BrownfieldScenario.md |
| Ambiguous Requirement | docs/10-AmbiguousScenario.md |
| ADR | docs/11-ArchitectureDecisionRecords.md |
| Test Strategy | docs/12-TestStrategy.md |
| Final Summary | docs/13-FinalEngineeringSummary.md |

---

# Author

Lakshmi Prasanna

AI-Assisted Software Engineering Assessment

Charles Schwab