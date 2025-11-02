package com.example.SkillSwap.dto.security;

public record UserRoleAssignmentRequestDTO(
        Long userId,
        Long roleId
) {}