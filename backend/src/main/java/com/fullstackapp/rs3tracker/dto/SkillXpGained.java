package com.fullstackapp.rs3tracker.dto;

import java.util.List;

public record SkillXpGained(int skillId, String skillName, List<MonthlyXpPoint> monthly, long totalXp) {}
