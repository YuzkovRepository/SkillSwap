package com.example.SkillSwap.dto.security;

public record PermissionResponseDTO(
        Long permissionsId,
        String permissionName,
        String description
) {}
