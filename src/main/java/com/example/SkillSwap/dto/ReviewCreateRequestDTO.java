package com.example.SkillSwap.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReviewCreateRequestDTO (
        @NotNull Long targetUserId,
        @NotNull Long bookingId,
        @Min(1) @Max(5) Integer rating,
        String comment
) {}
