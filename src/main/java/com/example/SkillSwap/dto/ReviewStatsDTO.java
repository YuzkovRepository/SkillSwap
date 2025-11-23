package com.example.SkillSwap.dto;

import java.util.Map;

public record ReviewStatsDTO(
        Double averageRating,
        Integer totalReviews,
        Map<Integer, Integer> ratingDistribution,
        Integer fiveStarCount,
        Integer fourStarCount,
        Integer threeStarCount,
        Integer twoStarCount,
        Integer oneStarCount
) {}
