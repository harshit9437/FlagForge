package com.example.flagforge.dto;

import java.time.Instant;

public record FeatureFlagResponse(
        Long id,
        String name,
        String key,
        String description,
        Long projectId,
        Instant createdAt
) {}
