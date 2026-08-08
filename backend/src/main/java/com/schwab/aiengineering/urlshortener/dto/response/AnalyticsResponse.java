package com.schwab.aiengineering.urlshortener.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {

    private String shortCode;

    private String originalUrl;

    private Long clickCount;

    private Boolean active;

    private Boolean expired;

}