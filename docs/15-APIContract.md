POST
POST /api/v1/urls

Request:

{
"originalUrl": "https://example.com",
"customAlias": "example",
"expiryDate": "2026-12-31T23:59:59"
}

Response:

{
"originalUrl": "https://example.com",
"shortCode": "example",
"shortUrl": "http://localhost:8080/example",
"createdAt": "...",
"expiryDate": "..."
}

201 Created
400 Bad Request
409 Conflict
410 Gone