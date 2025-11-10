package com.example.SkillSwap.repository;

import com.example.SkillSwap.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {
    @Query(value = "SELECT has_overlapping_bookings(:offerId, :requestedStartDateTime, :requestedEndDateTime, :excludeBookingId)",
            nativeQuery = true)
    boolean hasOverlappingBookings(
            @Param("offerId") Long offerId,
            @Param("requestedStartDateTime") LocalDateTime requestedStartDateTime,
            @Param("requestedEndDateTime") LocalDateTime requestedEndDateTime,
            @Param("excludeBookingId") Long excludeBookingId
    );

    @Query(value = "SELECT has_overlapping_bookings(:offerId, :requestedStartDateTime, :requestedEndDateTime, NULL)",
            nativeQuery = true)
    boolean hasOverlappingBookings(
            @Param("offerId") Long offerId,
            @Param("requestedStartDateTime") LocalDateTime requestedStartDateTime,
            @Param("requestedEndDateTime") LocalDateTime requestedEndDateTime
    );

    @Query(value = """
        SELECT b.* FROM bookings b 
        WHERE 
            (b.status = 'CONFIRMED' AND b.scheduled_datetime <= :now)
            OR 
            (b.status = 'IN_PROCESS' AND (b.scheduled_datetime + (b.duration_minutes || ' minutes')::interval) <= :now)
        """, nativeQuery = true)
    List<Booking> findActiveBookingsForStatusUpdate(@Param("now") LocalDateTime now);
}
