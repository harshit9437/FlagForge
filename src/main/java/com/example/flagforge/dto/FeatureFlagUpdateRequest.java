package com.example.flagforge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FeatureFlagUpdateRequest (
        @NotBlank
        @Size(max=100)
        String name,
        @NotBlank
        @Size(max=100)
        String key,
        @Size(max = 500)
        String description
){ }
