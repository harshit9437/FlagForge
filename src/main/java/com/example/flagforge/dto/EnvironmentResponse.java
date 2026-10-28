package com.example.flagforge.dto;

import com.example.flagforge.entity.EnvironmentType;

import java.time.Instant;

public record EnvironmentResponse (
        Long id,
        EnvironmentType name,
        Instant createdAt
){ }
