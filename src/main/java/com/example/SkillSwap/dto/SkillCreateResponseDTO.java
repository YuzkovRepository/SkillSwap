package com.example.SkillSwap.dto;

public record SkillCreateResponseDTO(
        String name,
        String description,
        Long parentSkillId
) {}
