package com.fullstackapp.rs3tracker.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_returns404WithMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Player not found");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> resp = handler.handleNotFound(ex);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resp.getBody().status()).isEqualTo(404);
        assertThat(resp.getBody().error()).isEqualTo("Not Found");
        assertThat(resp.getBody().message()).isEqualTo("Player not found");
        assertThat(resp.getBody().timestamp()).isNotNull();
    }

    @Test
    void handleConflict_returns409WithMessage() {
        ConflictException ex = new ConflictException("Character already claimed");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> resp = handler.handleConflict(ex);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(resp.getBody().status()).isEqualTo(409);
        assertThat(resp.getBody().error()).isEqualTo("Conflict");
        assertThat(resp.getBody().message()).isEqualTo("Character already claimed");
    }

    @Test
    void handleExternalApi_withKnownStatusCode_returnsProvidedStatus() {
        ExternalApiException ex = new ExternalApiException("Service unavailable", 503);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> resp = handler.handleExternalApi(ex);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(resp.getBody().status()).isEqualTo(503);
    }

    @Test
    void handleExternalApi_withUnresolvableStatusCode_returnsBadGateway() {
        ExternalApiException ex = new ExternalApiException("Unknown status", 999);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> resp = handler.handleExternalApi(ex);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(resp.getBody().status()).isEqualTo(502);
    }

    @Test
    void handleValidation_returnsBadRequestWithFieldErrorMessages() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("registerRequest", "email", "must be a valid email address"),
                new FieldError("registerRequest", "password", "size must be between 8 and 2147483647")
        ));

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> resp = handler.handleValidation(ex);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody().status()).isEqualTo(400);
        assertThat(resp.getBody().message()).contains("must be a valid email address");
        assertThat(resp.getBody().message()).contains("size must be between 8 and 2147483647");
    }

    @Test
    void handleResponseStatus_propagatesStatusCodeAndReason() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> resp = handler.handleResponseStatus(ex);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(resp.getBody().status()).isEqualTo(401);
        assertThat(resp.getBody().message()).isEqualTo("Invalid credentials");
    }

    @Test
    void handleRestClient_returnsBadGateway() {
        RestClientException ex = new RestClientException("Connection refused");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> resp = handler.handleRestClient(ex);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(resp.getBody().status()).isEqualTo(502);
        assertThat(resp.getBody().message()).isEqualTo("External API unavailable");
    }

    @Test
    void handleGeneric_returns500() {
        Exception ex = new RuntimeException("Something went wrong");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> resp = handler.handleGeneric(ex);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resp.getBody().status()).isEqualTo(500);
        assertThat(resp.getBody().message()).isEqualTo("An unexpected error occurred");
    }
}
