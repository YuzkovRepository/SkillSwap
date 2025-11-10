package com.example.SkillSwap.service;

import com.example.SkillSwap.dto.BookingCreateResponseDTO;
import com.example.SkillSwap.entity.Booking;
import com.example.SkillSwap.service.impl.NotificationServiceImpl;

public interface NotificationService {
    void notifyCustomerAboutConfirmation(Long customerId, BookingCreateResponseDTO booking);
    void notifyProviderAboutConfirmation(Long providerId, BookingCreateResponseDTO booking);
    void notifyCustomerAboutRejection(Long customerId, BookingCreateResponseDTO booking, String reason);

    void notifyLessonStarted(Long userId, Booking booking);
    void notifyLessonCompleted(Long userId, Booking booking);
    void sendStatusUpdate(Long userId, String type, String message, Booking booking);

    void sendNotification(Long userId, NotificationServiceImpl.NotificationType type, String title, String message, Object data);
}
