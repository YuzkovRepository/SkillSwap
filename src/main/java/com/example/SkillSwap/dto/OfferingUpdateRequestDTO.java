package com.example.SkillSwap.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;

public record OfferingUpdateRequestDTO(
        Long offerId,
        @Column(length = 100) String title,
        @Column(columnDefinition = "TEXT") String description,
        @Min(value = 1, message = "Price must be at least 1") Integer price,
        @Min(value = 1, message = "Minutes must be at least 1") Integer durationMinutes,
        @Column(columnDefinition = "TEXT") String address
) {}
