package com.example.SkillSwap.service;

import com.example.SkillSwap.dto.ReviewCreateRequestDTO;
import com.example.SkillSwap.dto.ReviewResponseDTO;
import com.example.SkillSwap.dto.ReviewStatsDTO;

import java.util.List;

public interface ReviewService {
    ReviewResponseDTO createReview(Long authorId, ReviewCreateRequestDTO request);
    ReviewStatsDTO getUserReviewStats(Long userId);
    List<ReviewResponseDTO> getUserReviews(Long userId);
    void toggleReviewVisibility(Long reviewId, Long userId);
}
