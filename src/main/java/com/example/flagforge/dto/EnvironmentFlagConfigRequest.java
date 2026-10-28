
package com.example.flagforge.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record EnvironmentFlagConfigRequest(

        @NotNull
        Boolean enabled,

        @Min(0)
        @Max(100)
        int rolloutPercentage

) {}
