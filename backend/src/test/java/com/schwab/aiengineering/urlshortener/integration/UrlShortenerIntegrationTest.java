package com.schwab.aiengineering.urlshortener.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schwab.aiengineering.urlshortener.dto.request.CreateShortUrlRequest;
import com.schwab.aiengineering.urlshortener.entity.UrlMapping;
import com.schwab.aiengineering.urlshortener.repository.UrlMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UrlShortenerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UrlMappingRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void shouldCreateShortUrlEndToEnd() throws Exception {

        CreateShortUrlRequest request =
                new CreateShortUrlRequest();

        request.setOriginalUrl("https://www.google.com");
        request.setCustomAlias("google");

        String response = mockMvc.perform(
                        post("/api/v1/urls")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode")
                        .value("google"))
                .andExpect(jsonPath("$.originalUrl")
                        .value("https://www.google.com"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json =
                objectMapper.readTree(response);

        assertEquals("google",
                json.get("shortCode").asText());

        assertTrue(repository
                .findByShortCode("google")
                .isPresent());
    }

    @Test
    void shouldRedirectToOriginalUrl() throws Exception {

        UrlMapping mapping = UrlMapping.builder()
                .originalUrl("https://www.google.com")
                .shortCode("google")
                .customAlias("google")
                .clickCount(0L)
                .active(true)
                .build();

        repository.save(mapping);

        mockMvc.perform(
                        get("/google"))
                .andExpect(status().isFound())
                .andExpect(header()
                        .string("Location",
                                "https://www.google.com"));

        UrlMapping updated =
                repository.findByShortCode("google")
                        .orElseThrow();

        assertEquals(1L,
                updated.getClickCount());
    }

    @Test
    void shouldReturnAnalytics() throws Exception {

        UrlMapping mapping = UrlMapping.builder()
                .originalUrl("https://www.google.com")
                .shortCode("google")
                .customAlias("google")
                .clickCount(15L)
                .active(true)
                .build();

        repository.save(mapping);

        mockMvc.perform(
                        get("/api/v1/urls/google/analytics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode")
                        .value("google"))
                .andExpect(jsonPath("$.originalUrl")
                        .value("https://www.google.com"))
                .andExpect(jsonPath("$.clickCount")
                        .value(15))
                .andExpect(jsonPath("$.active")
                        .value(true));
    }

    @Test
    void shouldSoftDeleteUrl() throws Exception {

        UrlMapping mapping = UrlMapping.builder()
                .originalUrl("https://www.google.com")
                .shortCode("google")
                .customAlias("google")
                .clickCount(0L)
                .active(true)
                .build();

        repository.save(mapping);

        mockMvc.perform(
                        delete("/api/v1/urls/google"))
                .andExpect(status().isNoContent());

        UrlMapping deleted =
                repository.findByShortCode("google")
                        .orElseThrow();

        assertFalse(deleted.getActive());
    }

    @Test
    void shouldReturn404ForUnknownShortCode() throws Exception {

        mockMvc.perform(
                        get("/does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.error")
                        .value("Not Found"));
    }

    @Test
    void shouldReturn404ForUnknownAnalyticsCode()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/urls/does-not-exist/analytics"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404));
    }

    @Test
    void shouldReturn404WhenDeletingUnknownCode()
            throws Exception {

        mockMvc.perform(
                        delete("/api/v1/urls/does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404));
    }

    @Test
    void shouldRejectInvalidCreateRequest()
            throws Exception {

        CreateShortUrlRequest request =
                new CreateShortUrlRequest();

        request.setOriginalUrl("");

        mockMvc.perform(
                        post("/api/v1/urls")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectDuplicateCustomAlias()
            throws Exception {

        UrlMapping mapping = UrlMapping.builder()
                .originalUrl("https://www.google.com")
                .shortCode("google")
                .customAlias("google")
                .active(true)
                .clickCount(0L)
                .build();

        repository.save(mapping);

        CreateShortUrlRequest request =
                new CreateShortUrlRequest();

        request.setOriginalUrl("https://www.github.com");
        request.setCustomAlias("google");

        mockMvc.perform(
                        post("/api/v1/urls")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status")
                        .value(409))
                .andExpect(jsonPath("$.error")
                        .value("Conflict"));
    }

    @Test
    void shouldRejectExpiredUrlOnRedirect()
            throws Exception {

        UrlMapping mapping = UrlMapping.builder()
                .originalUrl("https://www.google.com")
                .shortCode("expired")
                .active(true)
                .clickCount(0L)
                .expiryDate(LocalDateTime.now().minusDays(1))
                .build();

        repository.save(mapping);

        mockMvc.perform(
                        get("/expired"))
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.status")
                        .value(410))
                .andExpect(jsonPath("$.error")
                        .value("Gone"));
    }

    @Test
    void shouldNotRedirectInactiveUrl()
            throws Exception {

        UrlMapping mapping = UrlMapping.builder()
                .originalUrl("https://www.google.com")
                .shortCode("inactive")
                .active(false)
                .clickCount(0L)
                .build();

        repository.save(mapping);

        mockMvc.perform(
                        get("/inactive"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404));
    }
}