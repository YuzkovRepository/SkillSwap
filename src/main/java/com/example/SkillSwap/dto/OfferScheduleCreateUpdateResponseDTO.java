package com.example.SkillSwap.dto;

import java.time.LocalTime;

public record OfferScheduleCreateUpdateResponseDTO(
    Short dayOfWeek,
    LocalTime startTime,
    LocalTime endTime
){}
