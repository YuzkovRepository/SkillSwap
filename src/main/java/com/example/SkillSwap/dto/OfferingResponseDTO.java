package com.example.SkillSwap.dto;

public record OfferingResponseDTO(
        String title,
        String description,
        String skillName,
        Integer price,
        Integer durationMinutes,
        Integer maxParticipants,
        String typeOffer,
        String status,
        String address
) {}
