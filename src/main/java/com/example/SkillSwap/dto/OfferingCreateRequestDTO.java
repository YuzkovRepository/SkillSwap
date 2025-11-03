package com.example.SkillSwap.dto;

import jakarta.persistence.Column;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.Min;

public record OfferingCreateRequestDTO(
    Long userId,
    @Column(length = 100) String title,
    @Column(columnDefinition = "TEXT") String description,
    Long skillId,
    @Min(value = 1, message = "Price must be at least 1") Integer price,
    @Min(value = 1, message = "Minutes must be at least 1") Integer durationMinutes,
    String typeOffer,
    @Column(columnDefinition = "TEXT") String address
) {}
