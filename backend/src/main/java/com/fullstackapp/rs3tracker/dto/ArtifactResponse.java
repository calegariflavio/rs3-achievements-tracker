package com.fullstackapp.rs3tracker.dto;

import java.time.LocalDateTime;

public record ArtifactResponse(
        Long id,
        String username,
        String artifactName,
        String collectionName,
        String digSite,
        LocalDateTime collectedAt
) {}
