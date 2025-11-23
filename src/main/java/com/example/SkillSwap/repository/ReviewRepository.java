package com.example.SkillSwap.repository;

import com.example.SkillSwap.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Найти все отзывы о пользователе
    List<Review> findByTargetUserUserIdAndIsVisibleTrue(Long targetUserId);

    // Найти отзывы пользователя как автора
    List<Review> findByAuthorUserId(Long authorId);

    // Найти отзыв по бронированию (чтобы не было дублей)
    Optional<Review> findByAuthorUserIdAndTargetUserUserIdAndBookingId(Long authorId, Long targetUserId, Long bookingId);

    // Статистика отзывов
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.targetUser.userId = :userId AND r.isVisible = true")
    Optional<Double> findAverageRatingByTargetUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.targetUser.userId = :userId AND r.isVisible = true")
    Long countByTargetUser(@Param("userId") Long userId);

    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.targetUser.userId = :userId AND r.isVisible = true GROUP BY r.rating")
    List<Object[]> getRatingDistribution(@Param("userId") Long userId);

    // Проверить, можно ли оставить отзыв (бронирование завершено)
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.bookingId = :bookingId AND b.status = 'COMPLETED' AND (b.user.userId = :userId OR b.offer.user.userId = :userId)")
    boolean canUserReviewBooking(@Param("userId") Long userId, @Param("bookingId") Long bookingId);
}
