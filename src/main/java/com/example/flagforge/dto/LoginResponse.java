package com.example.flagforge.dto;

public record LoginResponse (
        String accessToken,
        String tokenType,
        String name,
        String email
){

}
