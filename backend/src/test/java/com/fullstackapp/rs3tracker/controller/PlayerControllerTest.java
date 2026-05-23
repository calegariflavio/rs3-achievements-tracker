package com.fullstackapp.rs3tracker.controller;

import com.fullstackapp.rs3tracker.config.SecurityConfig;
import com.fullstackapp.rs3tracker.dto.*;
import com.fullstackapp.rs3tracker.security.JwtAuthFilter;
import com.fullstackapp.rs3tracker.service.PlayerService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlayerController.class)
@Import(SecurityConfig.class)
class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private PlayerService playerService;

    @BeforeEach
    void setupFilter() throws Exception {
        doAnswer(inv -> {
            FilterChain chain = inv.getArgument(2);
            chain.doFilter(inv.getArgument(0), inv.getArgument(1));
            return null;
        }).when(jwtAuthFilter).doFilter(any(), any(), any());
    }

    @Test
    void getFullProfile_returnsPlayerProfile() throws Exception {
        PlayerProfileResponse profile = new PlayerProfileResponse(
                "Zezima", 138, 5_000_000L, 2595, 300, 10, 5, "1", false,
                List.of(), List.of(), List.of(), List.of());

        when(playerService.getFullProfile("Zezima")).thenReturn(profile);

        mockMvc.perform(get("/api/player/Zezima"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Zezima"))
                .andExpect(jsonPath("$.combatLevel").value(138));
    }

    @Test
    void getQuests_returnsQuestList() throws Exception {
        List<QuestData> quests = List.of(
                new QuestData("Cook's Assistant", "COMPLETED", 0, false, 1, true)
        );
        when(playerService.getQuests("Zezima")).thenReturn(quests);

        mockMvc.perform(get("/api/player/Zezima/quests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Cook's Assistant"));
    }

    @Test
    void getSkills_returnsSkillList() throws Exception {
        List<SkillValue> skills = List.of(new SkillValue(0, 99, 13_000_000L, 1));
        when(playerService.getSkills("Zezima")).thenReturn(skills);

        mockMvc.perform(get("/api/player/Zezima/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(0))
                .andExpect(jsonPath("$[0].level").value(99));
    }

    @Test
    void getXpGained_returnsXpGainedList() throws Exception {
        List<XpDataPoint> dataPoints = List.of(
                new XpDataPoint("2024-01-01", 10_000_000L),
                new XpDataPoint("2024-01-15", 10_500_000L));
        List<SkillXpGained> xp = List.of(new SkillXpGained(0, "Attack", dataPoints, 500_000L));
        when(playerService.getXpGained("Zezima", 30)).thenReturn(xp);

        mockMvc.perform(get("/api/player/Zezima/xp-gained"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].skillName").value("Attack"))
                .andExpect(jsonPath("$[0].xpGained").value(500_000));
    }

    @Test
    void getXpGained_withDaysParam_passesItToService() throws Exception {
        when(playerService.getXpGained("Zezima", 7)).thenReturn(List.of());

        mockMvc.perform(get("/api/player/Zezima/xp-gained?days=7"))
                .andExpect(status().isOk());
    }
}
