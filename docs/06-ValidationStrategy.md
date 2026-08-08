# Validation Strategy

## Objective

Ensure every engineering artifact meets production-quality expectations.

---

# Validation Levels

## Input Validation

- Mandatory fields
- URL format validation
- Alias uniqueness
- Expiration validation

---

## Business Validation

- Duplicate short codes
- Duplicate aliases
- Expired URLs
- Inactive URLs

---

## Testing

- Unit tests
- Integration tests
- Manual API testing

---

## Build Validation

- Maven build
- Dependency resolution
- Compilation verification

---

## API Validation

Swagger was used to verify:

- Request contracts
- Response contracts
- Error responses
- HTTP status codes

---

# Quality Checklist

Before every commit:

- Build successful
- Tests passing
- No compilation errors
- Documentation updated
- Engineer review completed