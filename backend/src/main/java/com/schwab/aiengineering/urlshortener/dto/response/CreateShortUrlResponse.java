package com.schwab.aiengineering.urlshortener.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateShortUrlResponse {

    private String originalUrl;

    private String shortCode;

    private String shortUrl;

    private LocalDateTime createdAt;

    private LocalDateTime expiryDate;

}