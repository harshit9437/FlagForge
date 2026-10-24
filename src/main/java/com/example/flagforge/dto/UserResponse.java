package com.example.flagforge.dto;
import java.time.Instant;

public record UserResponse(
        String name,
        String email,
        Instant createdAt
) {}
