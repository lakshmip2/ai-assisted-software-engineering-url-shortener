package com.schwab.aiengineering.urlshortener.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "url_mapping")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlMapping extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 2048)
    private String originalUrl;

    @Column(nullable = false, unique = true, length = 20)
    private String shortCode;

    @Column(unique = true, length = 50)
    private String customAlias;

    @Builder.Default
    private Long clickCount = 0L;

    private LocalDateTime expiryDate;

    @Builder.Default
    private Boolean active = true;

    @Version
    private Long version;

}