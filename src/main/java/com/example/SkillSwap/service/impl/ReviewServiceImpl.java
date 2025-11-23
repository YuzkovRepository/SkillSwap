package com.example.SkillSwap.service.impl;

import com.example.SkillSwap.dto.ReviewCreateRequestDTO;
import com.example.SkillSwap.dto.ReviewResponseDTO;
import com.example.SkillSwap.dto.ReviewStatsDTO;
import com.example.SkillSwap.entity.Booking;
import com.example.SkillSwap.entity.Review;
import com.example.SkillSwap.entity.User;
import com.example.SkillSwap.exception.CommonException;
import com.example.SkillSwap.repository.BookingRepository;
import com.example.SkillSwap.repository.ReviewRepository;
import com.example.SkillSwap.repository.UserRepository;
import com.example.SkillSwap.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;

    @Transactional
    public ReviewResponseDTO createReview(Long authorId, ReviewCreateRequestDTO request) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new CommonException("Author not found"));

        User targetUser = userRepository.findById(request.targetUserId())
                .orElseThrow(() -> new CommonException("Target user not found"));

        Booking booking = bookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new CommonException("Booking not found"));

        if (!reviewRepository.canUserReviewBooking(authorId, request.bookingId())) {
            throw new CommonException("You cannot review this booking");
        }

        if (reviewRepository.findByAuthorUserIdAndTargetUserUserIdAndBookingId(authorId, request.targetUserId(), request.bookingId()).isPresent()) {
            throw new CommonException("You have already reviewed this booking");
        }

        if (authorId.equals(request.targetUserId())) {
            throw new CommonException("You cannot review yourself");
        }

        Review review = new Review();
        review.setAuthor(author);
        review.setTargetUser(targetUser);
        review.setRating(request.rating());
        review.setComment(request.comment());
        review.setBooking(booking);

        Review savedReview = reviewRepository.save(review);

        log.info("Review created: {} stars from user {} to user {}",
                request.rating(), authorId, request.targetUserId());

        updateUserRating(targetUser.getUserId());

        notifyAboutNewReview(savedReview);

        return mapToDTO(savedReview);
    }

    @Transactional
    public List<ReviewResponseDTO> getUserReviews(Long userId) {
        List<Review> reviews = reviewRepository.findByTargetUserUserIdAndIsVisibleTrue(userId);
        return reviews.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewStatsDTO getUserReviewStats(Long userId) {
        Double averageRating = reviewRepository.findAverageRatingByTargetUser(userId).orElse(0.0);
        Long totalReviews = reviewRepository.countByTargetUser(userId);

        List<Object[]> distribution = reviewRepository.getRatingDistribution(userId);
        Map<Integer, Integer> ratingMap = new HashMap<>();

        for (Object[] row : distribution) {
            ratingMap.put((Integer) row[0], ((Long) row[1]).intValue());
        }

        return new ReviewStatsDTO(
                Math.round(averageRating * 10.0) / 10.0,
                totalReviews.intValue(),
                ratingMap,
                ratingMap.getOrDefault(5, 0),
                ratingMap.getOrDefault(4, 0),
                ratingMap.getOrDefault(3, 0),
                ratingMap.getOrDefault(2, 0),
                ratingMap.getOrDefault(1, 0)
        );
    }


    @Transactional
    public void toggleReviewVisibility(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CommonException("Review not found"));

        if (!review.getAuthor().getUserId().equals(userId) &&
                !review.getTargetUser().getUserId().equals(userId)) {
            throw new CommonException("Access denied");
        }

        review.setVisible(!review.isVisible());
        reviewRepository.save(review);

        updateUserRating(review.getTargetUser().getUserId());

        log.info("Review {} visibility changed to {}", reviewId, review.isVisible());
    }


    private void updateUserRating(Long userId) {
        Double newRating = reviewRepository.findAverageRatingByTargetUser(userId).orElse(0.0);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException("User not found"));

        user.setRating(BigDecimal.valueOf(newRating));
        userRepository.save(user);

        log.debug("User {} rating updated to {}", userId, newRating);
    }

    private void notifyAboutNewReview(Review review) {
        String message = String.format(
                "Вам оставили отзыв!\n\nОт: %s %s\nОценка: %d/5\nКомментарий: %s\n\n" +
                        "Спасибо за вашу работу!",
                review.getAuthor().getFirstName(),
                review.getAuthor().getLastName(),
                review.getRating(),
                review.getComment() != null ? review.getComment() : "Без комментария"
        );

        notificationService.sendNotification(
                review.getTargetUser().getUserId(),
                NotificationServiceImpl.NotificationType.NEW_REVIEW,
                "Новый отзыв",
                message,
                Map.of(
                        "reviewId", review.getReviewId(),
                        "rating", review.getRating(),
                        "authorName", review.getAuthor().getFirstName() + " " + review.getAuthor().getLastName()
                )
        );
    }

    private ReviewResponseDTO mapToDTO(Review review) {
        return new ReviewResponseDTO(
                review.getReviewId(),
                review.getAuthor().getUserId(),
                review.getAuthor().getFirstName() + " " + review.getAuthor().getLastName(),
                review.getTargetUser().getUserId(),
                review.getTargetUser().getFirstName() + " " + review.getTargetUser().getLastName(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                review.isVisible()
        );
    }
}
