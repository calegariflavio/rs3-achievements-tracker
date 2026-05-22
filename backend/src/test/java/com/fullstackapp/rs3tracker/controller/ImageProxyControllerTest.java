package com.fullstackapp.rs3tracker.controller;

import com.fullstackapp.rs3tracker.config.SecurityConfig;
import com.fullstackapp.rs3tracker.security.JwtAuthFilter;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImageProxyController.class)
@Import(SecurityConfig.class)
class ImageProxyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private RestTemplate restTemplate;

    @BeforeEach
    void setupFilter() throws Exception {
        doAnswer(inv -> {
            FilterChain chain = inv.getArgument(2);
            chain.doFilter(inv.getArgument(0), inv.getArgument(1));
            return null;
        }).when(jwtAuthFilter).doFilter(any(), any(), any());
    }

    @Test
    void proxyImage_success_returnsBytesWithCacheHeader() throws Exception {
        byte[] imageBytes = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47}; // PNG magic bytes
        ResponseEntity<byte[]> upstream = ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imageBytes);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(byte[].class)))
                .thenReturn(upstream);

        mockMvc.perform(get("/api/proxy/image")
                        .param("url", "https://runescape.wiki/images/example.png"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "public, max-age=86400"))
                .andExpect(content().contentType(MediaType.IMAGE_PNG));
    }

    @Test
    void proxyImage_remoteReturnsError_returns404() throws Exception {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(byte[].class)))
                .thenThrow(new RestClientException("connection refused"));

        mockMvc.perform(get("/api/proxy/image")
                        .param("url", "https://runescape.wiki/images/missing.png"))
                .andExpect(status().isNotFound());
    }
}
