package com.example.SkillSwap.service.impl;

import com.example.SkillSwap.dto.BookingCreateRequestDTO;
import com.example.SkillSwap.dto.BookingCreateResponseDTO;
import com.example.SkillSwap.entity.Booking;
import com.example.SkillSwap.entity.Offer;
import com.example.SkillSwap.entity.User;
import com.example.SkillSwap.exception.AvailabilityCheckResult;
import com.example.SkillSwap.exception.CommonException;
import com.example.SkillSwap.repository.BookingRepository;
import com.example.SkillSwap.repository.OfferRepository;
import com.example.SkillSwap.repository.UserRepository;
import com.example.SkillSwap.service.BookingService;
import com.example.SkillSwap.service.NotificationService;
import com.example.SkillSwap.service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);
    final private OfferAvailabilityServiceImpl availabilityService;
    final private BookingRepository bookingRepository;
    final private OfferRepository offerRepository;
    final private UserRepository userRepository;
    final private TransactionService transactionService;
    private final NotificationService notificationService;
    private final JitsiMeetService jitsiMeetService;

    @Transactional
    @Override
    public BookingCreateResponseDTO createBooking(BookingCreateRequestDTO bookingRequestDTO, Long customerId) {
        Offer offer = offerRepository.findById(bookingRequestDTO.offerId())
                .orElseThrow(() -> new CommonException("Offer not found"));

        User user = userRepository.findById(customerId)
                .orElseThrow(() -> new CommonException("User not found"));


        int durationMinutes = offer.getDurationMinutes();

        AvailabilityCheckResult availabilityResult = availabilityService.checkAvailability(bookingRequestDTO.offerId(),
                bookingRequestDTO.scheduledDateTime(), durationMinutes, null);

        logger.info("Availability check for offer {} at {}: {} - {}",
                bookingRequestDTO.offerId(), bookingRequestDTO.scheduledDateTime(),
                availabilityResult.isAvailable(), availabilityResult.getMessage());

        if (!availabilityResult.isAvailable()) {
            logger.warn("Booking not available for offer {} at {}: {}",
                    bookingRequestDTO.offerId(), bookingRequestDTO.scheduledDateTime(),
                    availabilityResult.getMessage());
            throw new CommonException(availabilityResult.getMessage());
        }

        Booking booking = new Booking();
        booking.setOffer(offer);
        booking.setUser(user);
        booking.setScheduledDatetime(bookingRequestDTO.scheduledDateTime());
        booking.setDurationMinutes(durationMinutes);
        booking.setCustomerNotes(bookingRequestDTO.customerNotes());
        booking.setTotalPrice(offer.getPrice());
        booking.setStatus(Booking.Status.PENDING);

        Booking savedBooking = bookingRepository.save(booking);

        logger.info("Booking created successfully with ID: {}", savedBooking.getBookingId());

        return mapToDTO(savedBooking, offer, user);
    }

    @Transactional
    public BookingCreateResponseDTO confirmBooking(Long bookingId, Long providerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CommonException("Booking not found"));

        if (!booking.getOffer().getUser().getUserId().equals(providerId)) {
            throw new CommonException("You cannot confirm this booking");
        }

        if (booking.getStatus() != Booking.Status.PENDING) {
            throw new CommonException("The booking has already been processed");
        }

        AvailabilityCheckResult availability = availabilityService.checkAvailability(
                booking.getOffer().getOfferId(),
                booking.getScheduledDatetime(),
                booking.getDurationMinutes(),
                bookingId
        );

        if (!availability.isAvailable()) {
            throw new CommonException("Time is no longer available: " + availability.getMessage());
        }

        transactionService.reservePayment(booking);

        String meetUrl = jitsiMeetService.createMeetingLink(bookingId);

        booking.setMeetingUrl(meetUrl);
        booking.setMeetingId("Jitsi-" + bookingId);
        booking.setStatus(Booking.Status.CONFIRMED);

        Booking savedBooking = bookingRepository.save(booking);

        BookingCreateResponseDTO response = mapToDTO(savedBooking, booking.getOffer(), booking.getUser());

        notificationService.notifyCustomerAboutConfirmation(savedBooking.getUser().getUserId(), response);
        notificationService.notifyProviderAboutConfirmation(providerId, response);

        return response;
    }

    @Transactional
    public BookingCreateResponseDTO rejectBooking(Long bookingId, Long providerId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new CommonException("Booking not found"));

        if (!booking.getOffer().getUser().getUserId().equals(providerId)) {
            throw new CommonException("You cannot reject this booking");
        }

        if (booking.getStatus() != Booking.Status.PENDING) {
            throw new CommonException("The booking has already been processed");
        } else if (booking.getStatus() == Booking.Status.CONFIRMED) {
            transactionService.refundPayment(booking);
        }

        booking.setStatus(Booking.Status.CANCELLED);
        if (reason != null && !reason.trim().isEmpty()) {
            booking.setCustomerNotes(
                    (booking.getCustomerNotes() != null ? booking.getCustomerNotes() + "\n" : "") +
                            "Причина отказа: " + reason
            );
        }

        Booking savedBooking = bookingRepository.save(booking);
        BookingCreateResponseDTO response = mapToDTO(savedBooking, savedBooking.getOffer(), savedBooking.getUser());
        notificationService.notifyCustomerAboutRejection(savedBooking.getUser().getUserId(), response, reason);

        return response;
    }

    private BookingCreateResponseDTO mapToDTO(Booking booking, Offer offer, User user) {
        boolean canJoinMeeting = jitsiMeetService.canJoinMeeting(
                booking.getScheduledDatetime(),
                booking.getDurationMinutes()
        );

        return new BookingCreateResponseDTO(
                offer.getOfferId(),
                offer.getTitle(),
                user.getFirstName() + " " + user.getSurname(),
                user.getEmail(),
                booking.getScheduledDatetime(),
                booking.getScheduledDatetime().plusMinutes(booking.getDurationMinutes()),
                booking.getDurationMinutes(),
                booking.getTotalPrice(),
                booking.getStatus().toString(),
                booking.getCustomerNotes(),
                booking.getCreatedAt(),
                booking.getMeetingUrl(),
                canJoinMeeting
        );
    }
}
