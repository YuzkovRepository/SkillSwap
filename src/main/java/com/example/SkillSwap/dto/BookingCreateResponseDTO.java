package com.example.SkillSwap.dto;

import java.time.LocalDateTime;

public record BookingCreateResponseDTO(
        Long offerId,
        String offerTitle,
        String customerName,
        String customerEmail,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        int durationMinutes,
        int price,
        String status,
        String customerNotes,
        LocalDateTime createdAt
){}
