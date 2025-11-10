package com.example.SkillSwap.dto;

import java.time.LocalDateTime;

public record NotificationDTO(
        String type,
        String title,
        String message,
        Object data,
        LocalDateTime timestamp
) {}
