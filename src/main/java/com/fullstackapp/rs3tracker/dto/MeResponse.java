package com.fullstackapp.rs3tracker.dto;

import java.time.LocalDateTime;
import java.util.List;

public record MeResponse(String email, LocalDateTime createdAt, List<String> claimedCharacters) {}
