package com.example.SkillSwap.dto.security;

public record RegisterResponseDTO(
        String login,
        String email,
        String phone
) {}
