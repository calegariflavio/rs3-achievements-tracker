package com.fullstackapp.rs3tracker.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private static final String TEST_SECRET =
            "Y2hhbmdlLW1lLXVzZS1hLXJlYWwtMjU2LWJpdC1zZWNyZXQtaW4tcHJvZHVjdGlvbg==";

    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() throws Exception {
        jwtUtil = new JwtUtil();
        Field secretField = JwtUtil.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtUtil, TEST_SECRET);
    }

    @Test
    void generateToken_returnsNonNullToken() {
        String token = jwtUtil.generateToken("user@example.com");
        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void validateToken_withValidToken_returnsEmail() {
        String email = "user@example.com";
        String token = jwtUtil.generateToken(email);
        assertThat(jwtUtil.validateToken(token)).isEqualTo(email);
    }

    @Test
    void validateToken_withGarbageToken_returnsNull() {
        assertThat(jwtUtil.validateToken("not.a.valid.jwt")).isNull();
    }

    @Test
    void validateToken_withTamperedSignature_returnsNull() {
        String token = jwtUtil.generateToken("user@example.com");
        String tampered = token.substring(0, token.length() - 4) + "XXXX";
        assertThat(jwtUtil.validateToken(tampered)).isNull();
    }

    @Test
    void generateToken_differentEmails_produceDifferentTokens() {
        String t1 = jwtUtil.generateToken("a@a.com");
        String t2 = jwtUtil.generateToken("b@b.com");
        assertThat(t1).isNotEqualTo(t2);
    }
}
