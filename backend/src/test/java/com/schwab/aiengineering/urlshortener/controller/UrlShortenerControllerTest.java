package com.schwab.aiengineering.urlshortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schwab.aiengineering.urlshortener.dto.request.CreateShortUrlRequest;
import com.schwab.aiengineering.urlshortener.dto.response.AnalyticsResponse;
import com.schwab.aiengineering.urlshortener.dto.response.CreateShortUrlResponse;
import com.schwab.aiengineering.urlshortener.exception.UrlNotFoundException;
import com.schwab.aiengineering.urlshortener.service.UrlShortenerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(UrlShortenerController.class)
class UrlShortenerControllerTest {

    @Autowired
    private MockMvc mockMvc;



    @MockitoBean
    private UrlShortenerService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateShortUrl() throws Exception {

        CreateShortUrlRequest request = new CreateShortUrlRequest();
        request.setOriginalUrl("https://google.com");
        request.setCustomAlias("google");
        request.setExpiryDate(LocalDateTime.now().plusDays(10));

        CreateShortUrlResponse response =
                CreateShortUrlResponse.builder()
                        .shortCode("google")
                        .shortUrl("http://localhost:8080/google")
                        .originalUrl("https://google.com")
                        .build();

        when(service.createShortUrl(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode").value("google"))
                .andExpect(jsonPath("$.shortUrl")
                        .value("http://localhost:8080/google"));
    }

    @Test
    void shouldReturn400WhenInvalidRequest() throws Exception {

        CreateShortUrlRequest request = new CreateShortUrlRequest();

        request.setOriginalUrl("");

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRedirectSuccessfully() throws Exception {

        when(service.redirect("google"))
                .thenReturn("https://google.com");

        mockMvc.perform(get("/google"))
                .andExpect(status().isFound())
                .andExpect(header()
                        .string("Location",
                                "https://google.com"));
    }

    @Test
    void shouldReturn404WhenShortCodeNotFound() throws Exception {

        when(service.redirect("abc"))
                .thenThrow(new UrlNotFoundException("Not Found"));

        mockMvc.perform(get("/abc"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnAnalytics() throws Exception {

        AnalyticsResponse response =
                AnalyticsResponse.builder()
                        .shortCode("google")
                        .originalUrl("https://google.com")
                        .clickCount(12L)
                        .active(true)
                        .expired(false)
                        .build();

        when(service.getAnalytics("google"))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/urls/google/analytics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clickCount")
                        .value(12))
                .andExpect(jsonPath("$.active")
                        .value(true));
    }

    @Test
    void shouldReturn404ForAnalytics() throws Exception {

        when(service.getAnalytics("abc"))
                .thenThrow(new UrlNotFoundException("Not Found"));

        mockMvc.perform(get("/api/v1/urls/abc/analytics"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteSuccessfully() throws Exception {

        doNothing().when(service).delete("google");

        mockMvc.perform(delete("/api/v1/urls/google"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenDeletingUnknownCode() throws Exception {

        doThrow(new UrlNotFoundException("Not Found"))
                .when(service)
                .delete("abc");

        mockMvc.perform(delete("/api/v1/urls/abc"))
                .andExpect(status().isNotFound());
    }
}