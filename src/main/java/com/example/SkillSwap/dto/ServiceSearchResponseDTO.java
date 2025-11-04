package com.example.SkillSwap.dto;

import java.math.BigDecimal;

public record ServiceSearchResponseDTO(
        Long id,
        String title,
        Integer price,
        String description,
        String providerName,
        BigDecimal providerRating,
        String skillName
) {}
