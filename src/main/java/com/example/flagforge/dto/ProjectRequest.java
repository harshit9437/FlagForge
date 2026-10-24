package com.example.flagforge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
        @Size(max = 100)
        @NotBlank
        String name,
        @Size(max = 500)
        String description
){
}
