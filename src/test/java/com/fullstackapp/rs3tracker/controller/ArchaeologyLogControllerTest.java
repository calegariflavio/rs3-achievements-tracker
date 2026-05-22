package com.fullstackapp.rs3tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullstackapp.rs3tracker.config.SecurityConfig;
import com.fullstackapp.rs3tracker.dto.ArtifactRequest;
import com.fullstackapp.rs3tracker.dto.ArtifactResponse;
import com.fullstackapp.rs3tracker.security.JwtAuthFilter;
import com.fullstackapp.rs3tracker.service.ArchaeologyLogService;
import com.fullstackapp.rs3tracker.service.CharacterOwnershipService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArchaeologyLogController.class)
@Import(SecurityConfig.class)
class ArchaeologyLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private ArchaeologyLogService archaeologyLogService;

    @MockBean
    private CharacterOwnershipService characterOwnershipService;

    @BeforeEach
    void setupFilter() throws Exception {
        doAnswer(inv -> {
            FilterChain chain = inv.getArgument(2);
            chain.doFilter(inv.getArgument(0), inv.getArgument(1));
            return null;
        }).when(jwtAuthFilter).doFilter(any(), any(), any());
    }

    private UsernamePasswordAuthenticationToken authenticatedUser(String email) {
        return new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
    }

    // ── addArtifact ───────────────────────────────────────────────────────────

    @Test
    void addArtifact_ownerAuthenticated_returns201() throws Exception {
        when(characterOwnershipService.isOwner("user@test.com", "zezima")).thenReturn(true);

        ArtifactResponse response = new ArtifactResponse(
                1L, "zezima", "Sword hilt", "Collection A", "Kharid-et",
                LocalDateTime.of(2024, 1, 1, 0, 0));
        when(archaeologyLogService.addArtifact(eq("zezima"), any(ArtifactRequest.class)))
                .thenReturn(response);

        String body = objectMapper.writeValueAsString(
                new ArtifactRequest("Sword hilt", "Collection A", "Kharid-et"));

        mockMvc.perform(post("/api/archaeology/zezima/artifact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(authentication(authenticatedUser("user@test.com"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.artifactName").value("Sword hilt"))
                .andExpect(jsonPath("$.username").value("zezima"));
    }

    @Test
    void addArtifact_notOwner_returns403() throws Exception {
        when(characterOwnershipService.isOwner("other@test.com", "zezima")).thenReturn(false);

        String body = objectMapper.writeValueAsString(
                new ArtifactRequest("Sword hilt", "Collection A", "Kharid-et"));

        mockMvc.perform(post("/api/archaeology/zezima/artifact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(authentication(authenticatedUser("other@test.com"))))
                .andExpect(status().isForbidden());
    }

    // ── getArtifacts ──────────────────────────────────────────────────────────

    @Test
    void getArtifacts_returnsListOfNames() throws Exception {
        when(archaeologyLogService.getArtifacts("zezima"))
                .thenReturn(List.of("Sword hilt", "Bronze statuette"));

        mockMvc.perform(get("/api/archaeology/zezima"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Sword hilt"))
                .andExpect(jsonPath("$[1]").value("Bronze statuette"));
    }

    @Test
    void getArtifacts_emptyList_returns200WithEmptyArray() throws Exception {
        when(archaeologyLogService.getArtifacts("unknown")).thenReturn(List.of());

        mockMvc.perform(get("/api/archaeology/unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ── removeArtifact ────────────────────────────────────────────────────────

    @Test
    void removeArtifact_ownerAuthenticated_returns204() throws Exception {
        when(characterOwnershipService.isOwner("user@test.com", "zezima")).thenReturn(true);

        mockMvc.perform(delete("/api/archaeology/zezima/artifact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"artifactName\":\"Sword hilt\"}")
                        .with(authentication(authenticatedUser("user@test.com"))))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeArtifact_notOwner_returns403() throws Exception {
        when(characterOwnershipService.isOwner("other@test.com", "zezima")).thenReturn(false);

        mockMvc.perform(delete("/api/archaeology/zezima/artifact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"artifactName\":\"Sword hilt\"}")
                        .with(authentication(authenticatedUser("other@test.com"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void verifyOwnership_noAuthentication_returns403() throws Exception {
        // No authentication set — auth.getName() returns null-like value or auth is anonymous
        // The controller checks: email == null || !isOwner(email, username)
        // When auth.getName() == "anonymousUser" and isOwner returns false → 403
        when(characterOwnershipService.isOwner(any(), eq("zezima"))).thenReturn(false);

        mockMvc.perform(post("/api/archaeology/zezima/artifact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"artifactName\":\"Sword hilt\"}"))
                .andExpect(status().isForbidden());
    }
}
