package com.example.SkillSwap.dto;

import java.time.LocalDateTime;

public record ReviewResponseDTO(
        Long reviewId,
        Long authorId,
        String authorName,
        Long targetUserId,
        String targetUserName,
        Integer rating,
        String comment,
        LocalDateTime createdAt,
        boolean isVisible
) { }
