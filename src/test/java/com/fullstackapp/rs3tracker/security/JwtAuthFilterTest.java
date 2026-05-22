package com.fullstackapp.rs3tracker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void resetContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void noHeader_chainContinues_noAuthSet() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void nonBearerHeader_chainContinues_noAuthSet() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void bearerHeaderWithInvalidToken_chainContinues_noAuthSet() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer bad.token");
        when(jwtUtil.validateToken("bad.token")).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void bearerHeaderWithValidToken_setsAuthInContext() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer good.token");
        when(jwtUtil.validateToken("good.token")).thenReturn("user@example.com");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName())
                .isEqualTo("user@example.com");
    }

    @Test
    void bearerHeaderWithValidToken_whenContextAlreadyHasAuth_doesNotReplaceExistingAuth() throws Exception {
        UsernamePasswordAuthenticationToken existing =
                new UsernamePasswordAuthenticationToken("existing@example.com", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(existing);

        when(request.getHeader("Authorization")).thenReturn("Bearer good.token");
        when(jwtUtil.validateToken("good.token")).thenReturn("new@example.com");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication().getName())
                .isEqualTo("existing@example.com");
        verify(filterChain).doFilter(request, response);
    }
}
