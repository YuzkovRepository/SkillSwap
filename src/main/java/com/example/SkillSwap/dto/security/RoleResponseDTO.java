package com.example.SkillSwap.dto.security;

public record RoleResponseDTO(
        Long roleId,
        String roleName,
        String description
) {}