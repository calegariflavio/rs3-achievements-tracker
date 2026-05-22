package com.fullstackapp.rs3tracker.dto;

import jakarta.validation.constraints.NotBlank;

public record ClaimCharacterRequest(@NotBlank String characterName) {}
