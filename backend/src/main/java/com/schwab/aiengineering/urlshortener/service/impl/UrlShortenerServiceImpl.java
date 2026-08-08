package com.schwab.aiengineering.urlshortener.service.impl;

import com.schwab.aiengineering.urlshortener.dto.request.CreateShortUrlRequest;
import com.schwab.aiengineering.urlshortener.dto.response.AnalyticsResponse;
import com.schwab.aiengineering.urlshortener.dto.response.CreateShortUrlResponse;
import com.schwab.aiengineering.urlshortener.entity.UrlMapping;
import com.schwab.aiengineering.urlshortener.exception.DuplicateAliasException;
import com.schwab.aiengineering.urlshortener.exception.UrlExpiredException;
import com.schwab.aiengineering.urlshortener.exception.UrlNotFoundException;
import com.schwab.aiengineering.urlshortener.repository.UrlMappingRepository;
import com.schwab.aiengineering.urlshortener.service.UrlShortenerService;
import com.schwab.aiengineering.urlshortener.util.ShortCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UrlShortenerServiceImpl implements UrlShortenerService {

    private final UrlMappingRepository repository;
    private final ShortCodeGenerator generator;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public CreateShortUrlResponse createShortUrl(CreateShortUrlRequest request) {

        String shortCode;

        if (request.getCustomAlias() != null &&
                !request.getCustomAlias().isBlank()) {

            if (repository.existsByCustomAlias(request.getCustomAlias())) {
                throw new DuplicateAliasException(
                        "Custom alias already exists.");
            }

            shortCode = request.getCustomAlias();

        } else {

            do {
                shortCode = generator.generate();
            }
            while (repository.existsByShortCode(shortCode));

        }

        UrlMapping mapping = UrlMapping.builder()
                .originalUrl(request.getOriginalUrl())
                .shortCode(shortCode)
                .customAlias(request.getCustomAlias())
                .expiryDate(request.getExpiryDate())
                .clickCount(0L)
                .active(true)
                .build();

        UrlMapping saved = repository.save(mapping);

        return CreateShortUrlResponse.builder()
                .originalUrl(saved.getOriginalUrl())
                .shortCode(saved.getShortCode())
                .shortUrl(baseUrl + "/" + saved.getShortCode())
                .createdAt(saved.getCreatedAt())
                .expiryDate(saved.getExpiryDate())
                .build();

    }

    @Override
    public String redirect(String shortCode) {

        UrlMapping mapping = repository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new UrlNotFoundException("Short URL not found"));

        if (!mapping.getActive()) {
            throw new UrlNotFoundException("URL is inactive");
        }

        if (mapping.getExpiryDate() != null &&
                mapping.getExpiryDate().isBefore(LocalDateTime.now())) {

            throw new UrlExpiredException("URL has expired");
        }

        mapping.setClickCount(mapping.getClickCount() + 1);

        repository.save(mapping);

        return mapping.getOriginalUrl();

    }

    @Override
    @Transactional(readOnly = true)
    public AnalyticsResponse getAnalytics(String shortCode) {

        UrlMapping mapping = repository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new UrlNotFoundException("Short URL not found"));

        boolean expired =
                mapping.getExpiryDate() != null &&
                        mapping.getExpiryDate().isBefore(LocalDateTime.now());

        return AnalyticsResponse.builder()
                .shortCode(mapping.getShortCode())
                .originalUrl(mapping.getOriginalUrl())
                .clickCount(mapping.getClickCount())
                .active(mapping.getActive())
                .expired(expired)
                .build();

    }

    @Override
    public void delete(String shortCode) {

        UrlMapping mapping = repository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new UrlNotFoundException("Short URL not found"));

        mapping.setActive(false);

        repository.save(mapping);

    }

}