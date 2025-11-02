package com.example.SkillSwap.dto.security;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "Login or Email cannot be null or empty") String loginOrEmail,
        String password
) {}