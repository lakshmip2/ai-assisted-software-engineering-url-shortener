| Requirement      | Implementation            | Test                  |
| ---------------- | ------------------------- | --------------------- |
| Create short URL | `UrlShortenerServiceImpl` | Service + Integration |
| Custom alias     | `UrlShortenerServiceImpl` | Duplicate alias test  |
| Redirect         | Controller + Service      | Redirect integration  |
| Analytics        | Service + Controller      | Analytics tests       |
| Expiration       | Service                   | Expiration test       |
| Soft delete      | Service                   | Delete test           |
| Validation       | DTO + Handler             | Validation test       |
| Error handling   | GlobalExceptionHandler    | Handler tests         |
