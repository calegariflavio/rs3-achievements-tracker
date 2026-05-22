package com.fullstackapp.rs3tracker.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionClassesTest {

    @Test
    void resourceNotFoundException_storesMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Artifact not found");
        assertThat(ex.getMessage()).isEqualTo("Artifact not found");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    void conflictException_storesMessage() {
        ConflictException ex = new ConflictException("Character already claimed");
        assertThat(ex.getMessage()).isEqualTo("Character already claimed");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    void externalApiException_storesMessageAndStatusCode() {
        ExternalApiException ex = new ExternalApiException("Profile is private", 403);
        assertThat(ex.getMessage()).isEqualTo("Profile is private");
        assertThat(ex.getStatusCode()).isEqualTo(403);
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    void externalApiException_differentStatusCodes() {
        assertThat(new ExternalApiException("Not found", 404).getStatusCode()).isEqualTo(404);
        assertThat(new ExternalApiException("Server error", 502).getStatusCode()).isEqualTo(502);
    }
}
