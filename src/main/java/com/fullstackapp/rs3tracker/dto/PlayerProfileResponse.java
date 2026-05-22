package com.fullstackapp.rs3tracker.dto;

import java.util.List;

public record PlayerProfileResponse(
        String username,
        int combatLevel,
        long totalXp,
        int totalSkillLevel,
        int questsComplete,
        int questsStarted,
        int questsNotStarted,
        String rank,
        boolean loggedIn,
        List<SkillValue> skills,
        List<ActivityData> recentActivities,
        List<QuestData> quests,
        List<HiscoreEntry> bossKills
) {}
