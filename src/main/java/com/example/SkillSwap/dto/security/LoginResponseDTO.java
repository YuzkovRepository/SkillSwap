package com.example.SkillSwap.dto.security;

public record LoginResponseDTO (
        String token,
        String login,
        String email
){}
