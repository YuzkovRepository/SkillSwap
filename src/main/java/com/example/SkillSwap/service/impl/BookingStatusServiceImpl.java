package com.example.SkillSwap.service.impl;

import com.example.SkillSwap.entity.Booking;
import com.example.SkillSwap.repository.BookingRepository;
import com.example.SkillSwap.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BookingStatusServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);
    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void updateBookingStatuses() {
        LocalDateTime now = LocalDateTime.now();

        List<Booking> activeBookings = bookingRepository.findActiveBookingsForStatusUpdate(now);

        for (Booking booking : activeBookings) {
            updateBookingStatus(booking, now);
        }

        logger.debug("Processed {} bookings for status update", activeBookings.size());
    }

    private void updateBookingStatus(Booking booking, LocalDateTime now) {
        LocalDateTime startTime = booking.getScheduledDatetime();
        LocalDateTime endTime = startTime.plusMinutes(booking.getDurationMinutes());

        if (booking.getStatus() == Booking.Status.CONFIRMED &&
                !now.isBefore(startTime) &&
                now.isBefore(endTime)) {
            startBooking(booking);
        }
        else if (booking.getStatus() == Booking.Status.IN_PROCESS &&
                now.isAfter(endTime)) {
            completeBooking(booking);
        }
    }

    private void startBooking(Booking booking) {
        try {
            booking.setStatus(Booking.Status.IN_PROCESS);
            bookingRepository.save(booking);

            logger.info("Booking {} started - IN_PROCESS", booking.getBookingId());

            // Уведомляем обоих участников используя новый метод
            notificationService.notifyLessonStarted(booking.getUser().getUserId(), booking);
            notificationService.notifyLessonStarted(booking.getOffer().getUser().getUserId(), booking);

        } catch (Exception e) {
            logger.error("Error starting booking {}: {}", booking.getBookingId(), e.getMessage());
        }
    }

    private void completeBooking(Booking booking) {
        try {
            booking.setStatus(Booking.Status.COMPLETED);
            bookingRepository.save(booking);

            logger.info("Booking {} completed", booking.getBookingId());

            notificationService.notifyLessonCompleted(booking.getUser().getUserId(), booking);
            notificationService.notifyLessonCompleted(booking.getOffer().getUser().getUserId(), booking);

        } catch (Exception e) {
            logger.error("Error completing booking {}: {}", booking.getBookingId(), e.getMessage());
        }
    }
}
