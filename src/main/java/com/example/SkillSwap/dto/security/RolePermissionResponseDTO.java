package com.example.SkillSwap.dto.security;


public record RolePermissionResponseDTO(
        String roleName,
        String permissionName
) {}