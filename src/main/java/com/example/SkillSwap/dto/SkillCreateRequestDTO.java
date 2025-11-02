package com.example.SkillSwap.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;

public record SkillCreateRequestDTO(
        @Size(max = 100)
        @Column(nullable = false)
        String name,
        @Column(columnDefinition = "TEXT", nullable = false)
        String description,
        Long parentSkillId
) {}
