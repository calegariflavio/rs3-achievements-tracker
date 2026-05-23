package com.fullstackapp.rs3tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullstackapp.rs3tracker.config.SecurityConfig;
import com.fullstackapp.rs3tracker.entity.Account;
import com.fullstackapp.rs3tracker.entity.CharacterClaim;
import com.fullstackapp.rs3tracker.repository.AccountRepository;
import com.fullstackapp.rs3tracker.repository.CharacterClaimRepository;
import com.fullstackapp.rs3tracker.security.JwtAuthFilter;
import com.fullstackapp.rs3tracker.security.JwtUtil;
import com.fullstackapp.rs3tracker.service.CharacterOwnershipService;
import com.fullstackapp.rs3tracker.service.EmailService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private CharacterClaimRepository characterClaimRepository;

    @MockBean
    private CharacterOwnershipService characterOwnershipService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private EmailService emailService;

    @BeforeEach
    void setupFilter() throws Exception {
        doAnswer(inv -> {
            FilterChain chain = inv.getArgument(2);
            chain.doFilter(inv.getArgument(0), inv.getArgument(1));
            return null;
        }).when(jwtAuthFilter).doFilter(any(), any(), any());
    }

    // ── register ──────────────────────────────────────────────────────────────

    @Test
    void register_newEmail_returns202WithMessage() throws Exception {
        when(accountRepository.existsByEmail("user@test.com")).thenReturn(false);
        when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@test.com\",\"password\":\"password123\"}"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void register_newEmail_sendsVerificationEmail() throws Exception {
        when(accountRepository.existsByEmail("user@test.com")).thenReturn(false);
        when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@test.com\",\"password\":\"password123\"}"))
                .andExpect(status().isAccepted());

        verify(emailService).sendVerificationEmail(eq("user@test.com"), any());
    }

    @Test
    void register_duplicateEmail_returns409() throws Exception {
        when(accountRepository.existsByEmail("user@test.com")).thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@test.com\",\"password\":\"password123\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void register_invalidEmail_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"not-an-email\",\"password\":\"password123\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_shortPassword_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@test.com\",\"password\":\"short\"}"))
                .andExpect(status().isBadRequest());
    }

    // ── verify ────────────────────────────────────────────────────────────────

    @Test
    void verify_validToken_returns200WithToken() throws Exception {
        Account account = new Account();
        account.setEmail("user@test.com");
        account.setVerified(false);
        account.setVerificationToken("abc123");
        account.setTokenExpiresAt(LocalDateTime.now().plusHours(1));

        when(accountRepository.findByVerificationToken("abc123")).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(jwtUtil.generateToken("user@test.com")).thenReturn("jwt-token");

        mockMvc.perform(get("/api/auth/verify").param("token", "abc123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.email").value("user@test.com"));
    }

    @Test
    void verify_unknownToken_returns400() throws Exception {
        when(accountRepository.findByVerificationToken("bad-token")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/auth/verify").param("token", "bad-token"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void verify_expiredToken_returns400() throws Exception {
        Account account = new Account();
        account.setEmail("user@test.com");
        account.setVerified(false);
        account.setVerificationToken("old-token");
        account.setTokenExpiresAt(LocalDateTime.now().minusHours(1));

        when(accountRepository.findByVerificationToken("old-token")).thenReturn(Optional.of(account));

        mockMvc.perform(get("/api/auth/verify").param("token", "old-token"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void verify_alreadyVerifiedAccount_returns400() throws Exception {
        Account account = new Account();
        account.setEmail("user@test.com");
        account.setVerified(true);
        account.setVerificationToken("used-token");
        account.setTokenExpiresAt(LocalDateTime.now().plusHours(1));

        when(accountRepository.findByVerificationToken("used-token")).thenReturn(Optional.of(account));

        mockMvc.perform(get("/api/auth/verify").param("token", "used-token"))
                .andExpect(status().isBadRequest());
    }

    // ── login ─────────────────────────────────────────────────────────────────

    @Test
    void login_validCredentials_returns200WithToken() throws Exception {
        Account account = new Account();
        account.setEmail("user@test.com");
        account.setPassword(passwordEncoder.encode("password123"));
        account.setVerified(true);

        when(accountRepository.findByEmail("user@test.com")).thenReturn(Optional.of(account));
        when(jwtUtil.generateToken("user@test.com")).thenReturn("jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@test.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void login_unverifiedAccount_returns403() throws Exception {
        Account account = new Account();
        account.setEmail("user@test.com");
        account.setPassword(passwordEncoder.encode("password123"));
        account.setVerified(false);

        when(accountRepository.findByEmail("user@test.com")).thenReturn(Optional.of(account));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@test.com\",\"password\":\"password123\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void login_unknownEmail_returns401() throws Exception {
        when(accountRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ghost@test.com\",\"password\":\"password123\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_wrongPassword_returns401() throws Exception {
        Account account = new Account();
        account.setEmail("user@test.com");
        account.setPassword(passwordEncoder.encode("correctpassword"));
        when(accountRepository.findByEmail("user@test.com")).thenReturn(Optional.of(account));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@test.com\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized());
    }

    // ── claim ─────────────────────────────────────────────────────────────────

    @Test
    void claim_authenticatedUser_returns201() throws Exception {
        CharacterClaim claim = new CharacterClaim();
        claim.setCharacterName("zezima");
        claim.setClaimedAt(LocalDateTime.of(2024, 1, 1, 0, 0));

        when(characterOwnershipService.claimCharacter("user@test.com", "zezima")).thenReturn(claim);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "user@test.com", null, Collections.emptyList());

        mockMvc.perform(post("/api/auth/claim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"characterName\":\"Zezima\"}")
                        .with(authentication(auth)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.characterName").value("zezima"));
    }

    @Test
    void claim_unauthenticatedRequest_returns401() throws Exception {
        UsernamePasswordAuthenticationToken unauth =
                new UsernamePasswordAuthenticationToken("anon", null);

        mockMvc.perform(post("/api/auth/claim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"characterName\":\"Zezima\"}")
                        .with(authentication(unauth)))
                .andExpect(status().isUnauthorized());
    }

    // ── me ────────────────────────────────────────────────────────────────────

    @Test
    void me_authenticatedUser_returns200WithAccountInfo() throws Exception {
        Account account = new Account();
        account.setEmail("user@test.com");
        account.setCreatedAt(LocalDateTime.of(2024, 1, 1, 0, 0));

        CharacterClaim claim = new CharacterClaim();
        claim.setCharacterName("Zezima");

        when(accountRepository.findByEmail("user@test.com")).thenReturn(Optional.of(account));
        when(characterClaimRepository.findByAccountId(any())).thenReturn(List.of(claim));

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "user@test.com", null, Collections.emptyList());

        mockMvc.perform(get("/api/auth/me").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@test.com"))
                .andExpect(jsonPath("$.claimedCharacters[0]").value("zezima"));
    }

    @Test
    void me_unauthenticatedRequest_returns401() throws Exception {
        UsernamePasswordAuthenticationToken unauth =
                new UsernamePasswordAuthenticationToken("anon", null);

        mockMvc.perform(get("/api/auth/me").with(authentication(unauth)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_accountNotFoundInRepository_returns401() throws Exception {
        when(accountRepository.findByEmail("gone@test.com")).thenReturn(Optional.empty());

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "gone@test.com", null, Collections.emptyList());

        mockMvc.perform(get("/api/auth/me").with(authentication(auth)))
                .andExpect(status().isUnauthorized());
    }
}
