package com.example.SkillSwap.service.impl;

import com.example.SkillSwap.dto.BookingCreateResponseDTO;
import com.example.SkillSwap.dto.NotificationDTO;
import com.example.SkillSwap.entity.Booking;
import com.example.SkillSwap.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public void notifyCustomerAboutConfirmation(Long customerId, BookingCreateResponseDTO booking) {
        try {
            String message = String.format(
                    "✅ Ваше бронирование подтверждено!\n\n" +
                            "🎯 Услуга: %s\n" +
                            "📅 Дата: %s\n" +
                            "⏰ Время: %s - %s\n" +
                            "🔗 Ссылка на встречу: %s\n\n" +
                            "Вы можете подключиться за 15 минут до начала.",
                    booking.offerTitle(),
                    booking.startDateTime().toLocalDate(),
                    booking.startDateTime().toLocalTime(),
                    booking.endDateTime().toLocalTime(),
                    booking.meetingUrl()
            );

            sendNotification(
                    customerId,
                    NotificationType.BOOKING_CONFIRMED,
                    "Бронирование подтверждено",
                    message,
                    booking
            );

            log.info("A confirmation notification has been sent to the user {}", customerId);
        } catch (Exception e) {
            log.error("Error sending notification: {}", e.getMessage());
        }
    }

    public void notifyProviderAboutConfirmation(Long providerId, BookingCreateResponseDTO booking) {
        try {
            String message = String.format(
                    "✅ Вы подтвердили бронирование!\n\n" +
                            "🎯 Услуга: %s\n" +
                            "👤 Клиент: %s\n" +
                            "📅 Дата: %s\n" +
                            "⏰ Время: %s - %s\n" +
                            "🔗 Ссылка на встречу: %s",
                    booking.offerTitle(),
                    booking.customerName(),
                    booking.startDateTime().toLocalDate(),
                    booking.startDateTime().toLocalTime(),
                    booking.endDateTime().toLocalTime(),
                    booking.meetingUrl()
            );

            sendNotification(
                    providerId,
                    NotificationType.BOOKING_CONFIRMED,
                    "Бронирование подтверждено",
                    message,
                    booking
            );

            log.info("Notification to the performer has been sent to the user {}", providerId);
        } catch (Exception e) {
            log.error("Error sending notification: {}", e.getMessage());
        }
    }

    public void notifyCustomerAboutRejection(Long customerId, BookingCreateResponseDTO booking, String reason) {
        try {
            String message = reason != null ?
                    String.format("Бронирование на '%s' отклонено. Причина: %s", booking.offerTitle(), reason) :
                    String.format("Бронирование на '%s' отклонено исполнителем", booking.offerTitle());

            sendNotification(
                    customerId,
                    NotificationType.BOOKING_REJECTED,
                    "Бронирование отклонено",
                    message,
                    booking
            );

            log.info("Notification of rejection sent to the user {}", customerId);
        } catch (Exception e) {
            log.error("Error sending notification: {}", e.getMessage());
        }
    }

    @Override
    public void notifyLessonStarted(Long userId, Booking booking) {
        try {
            String message = String.format(
                    "🎬 Занятие началось!\n\n" +
                            "🎯 %s\n" +
                            "👥 Участники подключились\n" +
                            "⏰ Продолжительность: %d минут\n" +
                            "🔗 Комната: %s",
                    booking.getOffer().getTitle(),
                    booking.getDurationMinutes(),
                    booking.getMeetingUrl()
            );

            sendNotification(
                    userId,
                    NotificationType.LESSON_STARTED,
                    "Занятие началось",
                    message,
                    createBookingData(booking)
            );

            log.info("A notification about the start of the lesson has been sent to the user {}", userId);
        } catch (Exception e) {
            log.error("Error sending the class start notification: {}", e.getMessage());
        }
    }

    @Override
    public void notifyLessonCompleted(Long userId, Booking booking) {
        try {
            String message = String.format(
                    "✅ Занятие завершено!\n\n" +
                            "🎯 %s\n" +
                            "⏰ Продолжительность: %d минут\n" +
                            "💫 Спасибо за участие!",
                    booking.getOffer().getTitle(),
                    booking.getDurationMinutes()
            );

            sendNotification(
                    userId,
                    NotificationType.LESSON_COMPLETED,
                    "Занятие завершено",
                    message,
                    createBookingData(booking)
            );

            log.info("A notification about the end of the lesson has been sent to the user {}", userId);
        } catch (Exception e) {
            log.error("Error sending a notification about the end of a class: {}", e.getMessage());
        }
    }

    @Override
    public void sendStatusUpdate(Long userId, String type, String message, Booking booking) {
        try {
            NotificationType notificationType = mapToNotificationType(type);

            sendNotification(
                    userId,
                    notificationType,
                    getTitleByType(type),
                    message,
                    createBookingData(booking)
            );

            log.info("The update status has been sent to the user {}: {}", userId, type);
        } catch (Exception e) {
            log.error("Error sending update status: {}", e.getMessage());
        }
    }

    @Override
    public void sendNotification(Long userId, NotificationType type, String title, String message, Object data) {
        try {
            NotificationDTO notification = new NotificationDTO(
                    type.name(),
                    title,
                    message,
                    data,
                    LocalDateTime.now()
            );

            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/notifications",
                    notification
            );

            log.debug("The notification has been sent to the user {}: {}", userId, type);
        } catch (Exception e) {
            log.error("Error sending notification to user {}: {}", userId, e.getMessage());
        }
    }

    private NotificationType mapToNotificationType(String type) {
        try {
            return NotificationType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return NotificationType.SYSTEM_MESSAGE;
        }
    }

    private String getTitleByType(String type) {
        switch (type) {
            case "LESSON_STARTED": return "Занятие началось";
            case "LESSON_COMPLETED": return "Занятие завершено";
            case "BOOKING_CONFIRMED": return "Бронирование подтверждено";
            case "BOOKING_REJECTED": return "Бронирование отклонено";
            default: return "Уведомление";
        }
    }

    private Map<String, Object> createBookingData(Booking booking) {
        return Map.of(
                "bookingId", booking.getBookingId(),
                "status", booking.getStatus().toString(),
                "offerTitle", booking.getOffer().getTitle(),
                "startTime", booking.getScheduledDatetime(),
                "durationMinutes", booking.getDurationMinutes(),
                "meetingUrl", booking.getMeetingUrl()
        );
    }

    public enum NotificationType {
        BOOKING_CONFIRMED,
        BOOKING_REJECTED,
        LESSON_STARTED,
        LESSON_COMPLETED,
        SYSTEM_MESSAGE
    }
}