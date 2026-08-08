# Quality Gates

## Gate 1 — Compilation

mvn clean compile

## Gate 2 — Unit Tests

mvn test

## Gate 3 — Integration Tests

@SpringBootTest integration suite

## Gate 4 — Coverage

JaCoCo

Target:
- Line coverage >= 90%
- Branch coverage >= 85%

## Gate 5 — Static Analysis

Recommended:
- Checkstyle
- SpotBugs
- PMD

## Gate 6 — Security

- No credentials in source
- Dependency vulnerability scan
- No sensitive information supplied to AI

## Gate 7 — Human Review

Engineer reviews:
- Architecture
- API changes
- Persistence changes
- Security-sensitive changes

## Gate 8 — Release Readiness

All gates must pass before merge.