# Test Strategy

## Objectives

Verify functional correctness, reliability, and maintainability.

---

# Test Pyramid

- Unit Tests
- Integration Tests
- Manual API Testing

---

# Unit Tests

Validate:

- Short code generation
- Duplicate alias detection
- Analytics
- Redirect
- Soft delete

---

# Integration Tests

Validate:

- REST APIs
- Repository
- Database interactions

---

# Manual Testing

Swagger UI was used to verify:

- POST /api/v1/urls
- GET /{shortCode}
- GET /api/v1/urls/{shortCode}/analytics
- DELETE /api/v1/urls/{shortCode}

---

# Coverage Goals

- Service Layer > 90%
- Controller Layer > 80%

---

# Future Testing

- Load Testing
- Performance Testing
- Security Testing
- Chaos Testing