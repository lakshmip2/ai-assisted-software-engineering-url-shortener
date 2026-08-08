package com.schwab.aiengineering.urlshortener.exception;

import com.schwab.aiengineering.urlshortener.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);

        when(request.getRequestURI())
                .thenReturn("/api/v1/urls/google");
    }

    @Test
    void shouldHandleNotFoundException() {

        UrlNotFoundException exception =
                new UrlNotFoundException("Short URL not found");

        ErrorResponse response =
                handler.handleNotFound(exception, request);

        assertNotNull(response);

        assertEquals(404, response.getStatus());

        assertEquals("Not Found",
                response.getError());

        assertEquals("Short URL not found",
                response.getMessage());

        assertEquals("/api/v1/urls/google",
                response.getPath());

        assertNotNull(response.getTimestamp());
    }

    @Test
    void shouldHandleDuplicateAliasException() {

        DuplicateAliasException exception =
                new DuplicateAliasException("Alias already exists");

        ErrorResponse response =
                handler.handleDuplicate(exception, request);

        assertNotNull(response);

        assertEquals(409, response.getStatus());

        assertEquals("Conflict",
                response.getError());

        assertEquals("Alias already exists",
                response.getMessage());

        assertEquals("/api/v1/urls/google",
                response.getPath());

        assertNotNull(response.getTimestamp());
    }

    @Test
    void shouldHandleExpiredUrlException() {

        UrlExpiredException exception =
                new UrlExpiredException("URL has expired");

        ErrorResponse response =
                handler.handleExpired(exception, request);

        assertNotNull(response);

        assertEquals(410, response.getStatus());

        assertEquals("Gone",
                response.getError());

        assertEquals("URL has expired",
                response.getMessage());

        assertEquals("/api/v1/urls/google",
                response.getPath());

        assertNotNull(response.getTimestamp());
    }

    @Test
    void shouldHandleValidationException() {

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(
                        new Object(),
                        "createShortUrlRequest");

        bindingResult.addError(
                new FieldError(
                        "createShortUrlRequest",
                        "originalUrl",
                        "Original URL must not be blank"
                )
        );

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(
                        null,
                        bindingResult
                );

        ErrorResponse response =
                handler.validation(exception, request);

        assertNotNull(response);

        assertEquals(400, response.getStatus());

        assertEquals("Validation Failed",
                response.getError());

        assertEquals("Original URL must not be blank",
                response.getMessage());

        assertEquals("/api/v1/urls/google",
                response.getPath());

        assertNotNull(response.getTimestamp());
    }
}