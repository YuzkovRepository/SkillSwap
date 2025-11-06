package com.example.SkillSwap.repository;

import com.example.SkillSwap.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {
    @Query(value = "select * from has_overlapping_bookings(:offerId, :requestedStartDateTime, :requestedEndDateTime)", nativeQuery = true)
    boolean hasOverlappingBookings(
            @Param("offerId") Long offerId,
            @Param("requestedStartDateTime") LocalDateTime requestedStartDateTime,
            @Param("requestedEndDateTime") LocalDateTime requestedEndDateTime
    );
}
