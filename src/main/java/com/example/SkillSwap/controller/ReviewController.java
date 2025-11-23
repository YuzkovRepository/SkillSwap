package com.example.SkillSwap.controller;

import com.example.SkillSwap.dto.*;
import com.example.SkillSwap.entity.User;
import com.example.SkillSwap.exception.CommonException;
import com.example.SkillSwap.repository.UserRepository;
import com.example.SkillSwap.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody @Valid ReviewCreateRequestDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User author = userRepository.findByLogin(username)
                .orElseThrow(() -> new CommonException("User not found"));

        try {
            ReviewResponseDTO response = reviewService.createReview(author.getUserId(), request);
            return ResponseEntity.ok(response);
        } catch (CommonException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponseDTO>> getUserReviews(@PathVariable Long userId) {
        List<ReviewResponseDTO> reviews = reviewService.getUserReviews(userId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<ReviewStatsDTO> getUserReviewStats(@PathVariable Long userId) {
        ReviewStatsDTO stats = reviewService.getUserReviewStats(userId);
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/{reviewId}/toggle-visibility")
    public ResponseEntity<?> toggleReviewVisibility(@PathVariable Long reviewId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new CommonException("User not found"));

        try {
            reviewService.toggleReviewVisibility(reviewId, user.getUserId());
            return ResponseEntity.ok(new SuccessResponseDTO(HttpStatus.OK.value(), "Visibility updated"));
        } catch (CommonException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }
}
