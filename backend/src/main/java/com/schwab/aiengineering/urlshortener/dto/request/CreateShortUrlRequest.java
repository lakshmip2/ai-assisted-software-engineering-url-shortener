package com.schwab.aiengineering.urlshortener.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateShortUrlRequest {

    @NotBlank(message = "Original URL cannot be blank")
    @Pattern(
            regexp = "^(https?|ftp)://.*$",
            message = "Invalid URL format"
    )
    private String originalUrl;

    private String customAlias;

    private LocalDateTime expiryDate;

}