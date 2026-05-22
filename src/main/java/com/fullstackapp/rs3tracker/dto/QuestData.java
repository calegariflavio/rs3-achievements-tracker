package com.fullstackapp.rs3tracker.dto;

public record QuestData(
        String title,
        String status,
        int difficulty,
        boolean members,
        int questPoints,
        boolean userEligible
) {}
