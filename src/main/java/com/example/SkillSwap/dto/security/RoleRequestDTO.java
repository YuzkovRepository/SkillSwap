package com.example.SkillSwap.dto.security;

public record RoleRequestDTO(
        String roleName,
        Long parentRoleId,
        String description
) {}