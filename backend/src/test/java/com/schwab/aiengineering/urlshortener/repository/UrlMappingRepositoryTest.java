package com.schwab.aiengineering.urlshortener.repository;

import com.schwab.aiengineering.urlshortener.entity.UrlMapping;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UrlMappingRepositoryTest {

    @Autowired
    private UrlMappingRepository repository;

    @Test
    void shouldSaveUrlMapping() {

        UrlMapping mapping = UrlMapping.builder()
                .originalUrl("https://www.google.com")
                .shortCode("google")
                .customAlias("google")
               // .createdAt(LocalDateTime.now())
                .active(true)
                .clickCount(0L)
                .build();

        UrlMapping saved = repository.save(mapping);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("google", saved.getShortCode());
        assertEquals("https://www.google.com", saved.getOriginalUrl());
    }

    @Test
    void shouldFindByShortCode() {

        UrlMapping mapping = UrlMapping.builder()
                .originalUrl("https://www.google.com")
                .shortCode("g123")
                //.createdAt(LocalDateTime.now())
                .active(true)
                .clickCount(0L)
                .build();

        repository.save(mapping);

        var result = repository.findByShortCode("g123");

        assertTrue(result.isPresent());
        assertEquals("https://www.google.com",
                result.get().getOriginalUrl());
    }

    @Test
    void shouldReturnEmptyWhenShortCodeDoesNotExist() {

        var result =
                repository.findByShortCode("does-not-exist");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldCheckShortCodeExists() {

        UrlMapping mapping = UrlMapping.builder()
                .originalUrl("https://spring.io")
                .shortCode("spring")
               // .createdAt(LocalDateTime.now())
                .active(true)
                .clickCount(0L)
                .build();

        repository.save(mapping);

        assertTrue(repository.existsByShortCode("spring"));
    }

    @Test
    void shouldReturnFalseWhenShortCodeDoesNotExist() {

        assertFalse(
                repository.existsByShortCode("unknown"));
    }

    @Test
    void shouldCheckCustomAliasExists() {

        UrlMapping mapping = UrlMapping.builder()
                .originalUrl("https://github.com")
                .shortCode("github1")
                .customAlias("github")
              //  .createdAt(LocalDateTime.now())
                .active(true)
                .clickCount(0L)
                .build();

        repository.save(mapping);

        assertTrue(
                repository.existsByCustomAlias("github"));
    }

    @Test
    void shouldReturnFalseWhenCustomAliasDoesNotExist() {

        assertFalse(
                repository.existsByCustomAlias("unknown-alias"));
    }
}