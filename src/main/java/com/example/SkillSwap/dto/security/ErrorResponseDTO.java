package com.example.SkillSwap.dto.security;

public record ErrorResponseDTO(
        int status,
        String message
) {}