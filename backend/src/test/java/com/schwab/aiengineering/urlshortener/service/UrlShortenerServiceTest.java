package com.schwab.aiengineering.urlshortener.service;

import com.schwab.aiengineering.urlshortener.dto.request.CreateShortUrlRequest;
import com.schwab.aiengineering.urlshortener.dto.response.AnalyticsResponse;
import com.schwab.aiengineering.urlshortener.dto.response.CreateShortUrlResponse;
import com.schwab.aiengineering.urlshortener.entity.UrlMapping;
import com.schwab.aiengineering.urlshortener.exception.DuplicateAliasException;
import com.schwab.aiengineering.urlshortener.exception.UrlExpiredException;
import com.schwab.aiengineering.urlshortener.exception.UrlNotFoundException;
import com.schwab.aiengineering.urlshortener.repository.UrlMappingRepository;
import com.schwab.aiengineering.urlshortener.service.impl.UrlShortenerServiceImpl;
import com.schwab.aiengineering.urlshortener.util.ShortCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceTest {

    @Mock
    private UrlMappingRepository repository;

    @Mock
    private ShortCodeGenerator generator;

    @InjectMocks
    private UrlShortenerServiceImpl service;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(service,
                "baseUrl",
                "http://localhost:8080");
    }

    @Test
    void shouldCreateShortUrl() {

        CreateShortUrlRequest request =
                CreateShortUrlRequest.builder()
                        .originalUrl("https://openai.com")
                        .build();

        when(generator.generate()).thenReturn("abc1234");

        when(repository.existsByShortCode(anyString()))
                .thenReturn(false);

        UrlMapping mapping =
                UrlMapping.builder()
                        .shortCode("abc1234")
                        .originalUrl(request.getOriginalUrl())
                        .active(true)
                        .build();

        when(repository.save(any()))
                .thenReturn(mapping);

        CreateShortUrlResponse response =
                service.createShortUrl(request);

        assertEquals("abc1234",
                response.getShortCode());

        verify(repository, times(1))
                .save(any());

    }

    @Test
    void shouldThrowDuplicateAliasException() {

        CreateShortUrlRequest request =
                CreateShortUrlRequest.builder()
                        .originalUrl("https://google.com")
                        .customAlias("google")
                        .build();

        when(repository.existsByCustomAlias("google"))
                .thenReturn(true);

        assertThrows(
                DuplicateAliasException.class,
                () -> service.createShortUrl(request)
        );

    }
    @Test
    void shouldCreateShortUrlUsingCustomAlias() {

        CreateShortUrlRequest request = new CreateShortUrlRequest();
        request.setOriginalUrl("https://google.com");
        request.setCustomAlias("google");

        request.setExpiryDate(LocalDateTime.now().plusDays(10));

        when(repository.existsByCustomAlias("google"))
                .thenReturn(false);

        UrlMapping saved = UrlMapping.builder()
                .originalUrl(request.getOriginalUrl())
                .shortCode("google")
                .customAlias("google")
                .expiryDate(request.getExpiryDate())
                //.createdAt(LocalDateTime.now())
                .active(true)
                .clickCount(0L)
                .build();

        when(repository.save(any(UrlMapping.class)))
                .thenReturn(saved);

        CreateShortUrlResponse response =
                service.createShortUrl(request);

        assertNotNull(response);

        assertEquals("google",
                response.getShortCode());

        assertEquals(
                "http://localhost:8080/google",
                response.getShortUrl());

        verify(repository).existsByCustomAlias("google");
        verify(repository).save(any(UrlMapping.class));
    }
    @Test
    void shouldThrowExceptionWhenAliasAlreadyExists() {

        CreateShortUrlRequest request = new CreateShortUrlRequest();

        request.setOriginalUrl("https://google.com");
        request.setCustomAlias("google");

        when(repository.existsByCustomAlias("google"))
                .thenReturn(true);

        assertThrows(
                DuplicateAliasException.class,
                () -> service.createShortUrl(request));

        verify(repository).existsByCustomAlias("google");

        verify(repository, never()).save(any());
    }
    @Test
    void shouldGenerateShortCodeWhenAliasNotProvided() {

        CreateShortUrlRequest request = new CreateShortUrlRequest();

        request.setOriginalUrl("https://github.com");

        when(generator.generate())
                .thenReturn("ABC1234");

        when(repository.existsByShortCode("ABC1234"))
                .thenReturn(false);

        UrlMapping saved = UrlMapping.builder()
                .originalUrl(request.getOriginalUrl())
                .shortCode("ABC1234")
                //.createdAt(LocalDateTime.now())
                .active(true)
                .clickCount(0L)
                .build();

        when(repository.save(any()))
                .thenReturn(saved);

        CreateShortUrlResponse response =
                service.createShortUrl(request);

        assertEquals(
                "ABC1234",
                response.getShortCode());

        verify(generator).generate();
        verify(repository).save(any());
    }
    @Test
    void shouldRetryWhenGeneratedShortCodeAlreadyExists() {

        CreateShortUrlRequest request = new CreateShortUrlRequest();

        request.setOriginalUrl("https://openai.com");

        when(generator.generate())
                .thenReturn("AAA1111")
                .thenReturn("BBB2222");

        when(repository.existsByShortCode("AAA1111"))
                .thenReturn(true);

        when(repository.existsByShortCode("BBB2222"))
                .thenReturn(false);

        UrlMapping saved = UrlMapping.builder()
                .originalUrl(request.getOriginalUrl())
                .shortCode("BBB2222")
                //.createdAt(LocalDateTime.now())
                .active(true)
                .clickCount(0L)
                .build();

        when(repository.save(any()))
                .thenReturn(saved);

        CreateShortUrlResponse response =
                service.createShortUrl(request);

        assertEquals(
                "BBB2222",
                response.getShortCode());

        verify(generator, times(2)).generate();
    }

    @Test
    void shouldRedirectSuccessfully() {

        UrlMapping mapping = UrlMapping.builder()
                .shortCode("google")
                .originalUrl("https://google.com")
                .clickCount(5L)
                .active(true)
                .build();

        when(repository.findByShortCode("google"))
                .thenReturn(Optional.of(mapping));

        String url = service.redirect("google");

        assertEquals("https://google.com", url);

        assertEquals(6L, mapping.getClickCount());

        verify(repository).save(mapping);
    }

    @Test
    void shouldThrowWhenShortCodeNotFound() {

        when(repository.findByShortCode("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                UrlNotFoundException.class,
                () -> service.redirect("unknown"));

        verify(repository, never()).save(any());
    }
    @Test
    void shouldThrowWhenUrlInactive() {

        UrlMapping mapping = UrlMapping.builder()
                .shortCode("google")
                .originalUrl("https://google.com")
                .active(false)
                .build();

        when(repository.findByShortCode("google"))
                .thenReturn(Optional.of(mapping));

        assertThrows(
                UrlNotFoundException.class,
                () -> service.redirect("google"));

        verify(repository, never()).save(any());
    }
    @Test
    void shouldThrowWhenUrlExpired() {

        UrlMapping mapping = UrlMapping.builder()
                .shortCode("google")
                .originalUrl("https://google.com")
                .active(true)
                .expiryDate(LocalDateTime.now().minusDays(1))
                .build();

        when(repository.findByShortCode("google"))
                .thenReturn(Optional.of(mapping));

        assertThrows(
                UrlExpiredException.class,
                () -> service.redirect("google"));

        verify(repository, never()).save(any());
    }
    @Test
    void shouldReturnAnalytics() {

        UrlMapping mapping = UrlMapping.builder()
                .shortCode("google")
                .originalUrl("https://google.com")
                .clickCount(12L)
                .active(true)
                .build();

        when(repository.findByShortCode("google"))
                .thenReturn(Optional.of(mapping));

        AnalyticsResponse response =
                service.getAnalytics("google");

        assertNotNull(response);

        assertEquals("google", response.getShortCode());

        assertEquals(12L, response.getClickCount());

        assertTrue(response.getActive());

        assertFalse(response.getExpired());
    }
    @Test
    void shouldReturnExpiredAnalytics() {

        UrlMapping mapping = UrlMapping.builder()
                .shortCode("expired")
                .originalUrl("https://google.com")
                .clickCount(20L)
                .active(true)
                .expiryDate(LocalDateTime.now().minusHours(5))
                .build();

        when(repository.findByShortCode("expired"))
                .thenReturn(Optional.of(mapping));

        AnalyticsResponse response =
                service.getAnalytics("expired");

        assertTrue(response.getExpired());
    }
    @Test
    void shouldThrowWhenAnalyticsUrlNotFound() {

        when(repository.findByShortCode("xyz"))
                .thenReturn(Optional.empty());

        assertThrows(
                UrlNotFoundException.class,
                () -> service.getAnalytics("xyz"));
    }
    @Test
    void shouldDeleteSuccessfully() {

        UrlMapping mapping = UrlMapping.builder()
                .shortCode("google")
                .originalUrl("https://google.com")
                .active(true)
                .build();

        when(repository.findByShortCode("google"))
                .thenReturn(Optional.of(mapping));

        service.delete("google");

        assertFalse(mapping.getActive());

        verify(repository).save(mapping);
    }
    @Test
    void shouldThrowWhenDeleteUrlNotFound() {

        when(repository.findByShortCode("google"))
                .thenReturn(Optional.empty());

        assertThrows(
                UrlNotFoundException.class,
                () -> service.delete("google"));

        verify(repository, never()).save(any());
    }
    @Test
    void shouldPersistCorrectEntity() {

        CreateShortUrlRequest request = new CreateShortUrlRequest();
        request.setOriginalUrl("https://github.com");
        request.setCustomAlias("github");

        when(repository.existsByCustomAlias("github"))
                .thenReturn(false);

        UrlMapping saved = UrlMapping.builder()
                .shortCode("github")
                .originalUrl("https://github.com")
                .active(true)
                .build();

        when(repository.save(any()))
                .thenReturn(saved);

        service.createShortUrl(request);

        ArgumentCaptor<UrlMapping> captor =
                ArgumentCaptor.forClass(UrlMapping.class);

        verify(repository).save(captor.capture());

        UrlMapping entity = captor.getValue();

        assertEquals("github", entity.getShortCode());
        assertEquals("https://github.com", entity.getOriginalUrl());
        assertTrue(entity.getActive());
    }
    @Test
    void shouldGenerateCodeWhenAliasBlank() {

        CreateShortUrlRequest request = new CreateShortUrlRequest();

        request.setOriginalUrl("https://spring.io");
        request.setCustomAlias("");

        when(generator.generate())
                .thenReturn("SPR123");

        when(repository.existsByShortCode("SPR123"))
                .thenReturn(false);

        UrlMapping mapping = UrlMapping.builder()
                .shortCode("SPR123")
                .originalUrl("https://spring.io")
                .active(true)
                .build();

        when(repository.save(any()))
                .thenReturn(mapping);

        CreateShortUrlResponse response =
                service.createShortUrl(request);

        assertEquals("SPR123", response.getShortCode());
    }
    @Test
    void shouldCreateWithoutExpiryDate() {

        CreateShortUrlRequest request = new CreateShortUrlRequest();

        request.setOriginalUrl("https://openai.com");

        when(generator.generate())
                .thenReturn("OPEN123");

        when(repository.existsByShortCode("OPEN123"))
                .thenReturn(false);

        UrlMapping mapping = UrlMapping.builder()
                .shortCode("OPEN123")
                .originalUrl("https://openai.com")
                .active(true)
                .build();

        when(repository.save(any()))
                .thenReturn(mapping);

        CreateShortUrlResponse response =
                service.createShortUrl(request);

        assertNotNull(response);
    }
    @Test
    void shouldIncreaseClickCountOnRedirect() {

        UrlMapping mapping = UrlMapping.builder()
                .shortCode("abc")
                .originalUrl("https://google.com")
                .clickCount(99L)
                .active(true)
                .build();

        when(repository.findByShortCode("abc"))
                .thenReturn(Optional.of(mapping));

        service.redirect("abc");

        assertEquals(100L, mapping.getClickCount());

        verify(repository).save(mapping);
    }

}