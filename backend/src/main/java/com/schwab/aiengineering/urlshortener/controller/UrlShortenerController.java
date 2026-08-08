package com.schwab.aiengineering.urlshortener.controller;

import com.schwab.aiengineering.urlshortener.dto.request.CreateShortUrlRequest;
import com.schwab.aiengineering.urlshortener.dto.response.AnalyticsResponse;
import com.schwab.aiengineering.urlshortener.dto.response.CreateShortUrlResponse;
import com.schwab.aiengineering.urlshortener.service.UrlShortenerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@Tag(name = "URL Shortener API", description = "Operations for URL shortening service")
public class UrlShortenerController {

    private final UrlShortenerService service;

    @PostMapping("/api/v1/urls")
    @Operation(summary = "Create Short URL")
    public ResponseEntity<CreateShortUrlResponse> createShortUrl(
            @Valid @RequestBody CreateShortUrlRequest request) {

        CreateShortUrlResponse response =
                service.createShortUrl(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{shortCode}")
    @Operation(summary = "Redirect to Original URL")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode) {

        String originalUrl = service.redirect(shortCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(originalUrl));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/api/v1/urls/{shortCode}/analytics")
    @Operation(summary = "Retrieve URL Analytics")
    public ResponseEntity<AnalyticsResponse> analytics(
            @PathVariable String shortCode) {

        return ResponseEntity.ok(
                service.getAnalytics(shortCode));
    }

    @DeleteMapping("/api/v1/urls/{shortCode}")
    @Operation(summary = "Soft Delete URL")
    public ResponseEntity<Void> delete(
            @PathVariable String shortCode) {

        service.delete(shortCode);

        return ResponseEntity.noContent().build();
    }

}