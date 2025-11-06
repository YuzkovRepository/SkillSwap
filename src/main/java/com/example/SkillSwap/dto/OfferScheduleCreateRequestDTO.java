package com.example.SkillSwap.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalTime;

public record OfferScheduleCreateRequestDTO(
        Long offerId,
        @Min(1) @Max(7) Short dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {}
