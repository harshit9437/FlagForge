
package com.example.flagforge.dto;

import java.time.Instant;

public record EnvironmentFlagConfigResponse(
        Long id,
        Long featureFlagId,
        Long environmentId,
        boolean enabled,
        int rolloutPercentage,
        Instant createdAt,
        Instant updatedAt
) {}
