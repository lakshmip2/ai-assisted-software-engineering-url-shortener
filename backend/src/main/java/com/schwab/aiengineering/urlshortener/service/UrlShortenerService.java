package com.schwab.aiengineering.urlshortener.service;

import com.schwab.aiengineering.urlshortener.dto.request.CreateShortUrlRequest;
import com.schwab.aiengineering.urlshortener.dto.response.AnalyticsResponse;
import com.schwab.aiengineering.urlshortener.dto.response.CreateShortUrlResponse;

public interface UrlShortenerService {

    CreateShortUrlResponse createShortUrl(CreateShortUrlRequest request);

    String redirect(String shortCode);

    AnalyticsResponse getAnalytics(String shortCode);

    void delete(String shortCode);

}