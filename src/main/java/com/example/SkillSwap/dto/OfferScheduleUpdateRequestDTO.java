package com.example.SkillSwap.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalTime;

public record OfferScheduleUpdateRequestDTO(
        Long scheduleId,
        LocalTime startTime,
        LocalTime endTime
) {}
