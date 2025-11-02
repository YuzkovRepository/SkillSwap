package com.example.SkillSwap.dto.security;

public record RolePermissionAssignmentRequestDTO(
        Long roleId,
        Long permissionId
) {}