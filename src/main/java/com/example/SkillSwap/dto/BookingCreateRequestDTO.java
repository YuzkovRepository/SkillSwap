package com.example.SkillSwap.dto;

import java.time.LocalDateTime;

public record BookingCreateRequestDTO(
    Long offerId,
    LocalDateTime scheduledDateTime,
    String customerNotes
) {}
