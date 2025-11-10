package com.example.SkillSwap.service.impl;

import com.example.SkillSwap.dto.OfferScheduleCreateRequestDTO;
import com.example.SkillSwap.dto.OfferScheduleCreateUpdateResponseDTO;
import com.example.SkillSwap.dto.OfferScheduleUpdateRequestDTO;
import com.example.SkillSwap.entity.Offer;
import com.example.SkillSwap.entity.OfferAvailability;
import com.example.SkillSwap.exception.AvailabilityCheckResult;
import com.example.SkillSwap.exception.CommonException;
import com.example.SkillSwap.repository.BookingRepository;
import com.example.SkillSwap.repository.OfferAvailabilityRepository;
import com.example.SkillSwap.repository.OfferRepository;
import com.example.SkillSwap.service.BookingService;
import com.example.SkillSwap.service.OfferAvailabilityService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@AllArgsConstructor
public class OfferAvailabilityServiceImpl implements OfferAvailabilityService {
    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);
    final private OfferAvailabilityRepository availabilityRepository;
    final private OfferRepository offerRepository;
    final private BookingRepository bookingRepository;

    @Override
    public OfferScheduleCreateUpdateResponseDTO createOfferSchedule(OfferScheduleCreateRequestDTO request) {
        Offer offer = offerRepository.findById(request.offerId())
                .orElseThrow(() -> new CommonException("Service not found"));

        OfferAvailability offerAvailability = new OfferAvailability();
        offerAvailability.setOffer(offer);
        offerAvailability.setDayOfWeek(request.dayOfWeek());
        offerAvailability.setStartTime(request.startTime());
        offerAvailability.setEndTime(request.endTime());

        availabilityRepository.save(offerAvailability);
        return mapScheduleToDTO(offerAvailability);
    }

    @Override
    public void deleteOfferSchedule(Long id) {
        OfferAvailability offerAvailability = availabilityRepository.findById(id)
                .orElseThrow(() -> new CommonException("Schedule not found"));

        availabilityRepository.delete(offerAvailability);
    }

    @Override
    public OfferScheduleCreateUpdateResponseDTO updateOfferSchedule(OfferScheduleUpdateRequestDTO request) {
        OfferAvailability offerAvailability = availabilityRepository.findById(request.scheduleId())
                .orElseThrow(() -> new CommonException("Element of schedule not found"));

        offerAvailability.setStartTime(request.startTime());
        offerAvailability.setEndTime(request.endTime());
        availabilityRepository.save(offerAvailability);
        return mapScheduleToDTO(offerAvailability);
    }

    private OfferScheduleCreateUpdateResponseDTO mapScheduleToDTO(OfferAvailability offerAvailability){
        return new OfferScheduleCreateUpdateResponseDTO(
                offerAvailability.getDayOfWeek(),
                offerAvailability.getStartTime(),
                offerAvailability.getEndTime()
        );
    }

    public AvailabilityCheckResult checkAvailability(Long offerId, LocalDateTime requestedDateTime,
                                                     int durationMinutes, Long excludeBookingId) {
        DayOfWeek requestDayOfWeek = requestedDateTime.getDayOfWeek();
        Short dayOfWeek = (short) requestDayOfWeek.getValue();

        LocalTime startTime = requestedDateTime.toLocalTime();
        LocalTime endTime = startTime.plusMinutes(durationMinutes);

        logger.info("Checking availability for offer {} on day {}: {} to {}",
                offerId, dayOfWeek, startTime, endTime);

        List<Object[]> availabilityResults = availabilityRepository.findByOfferOfferIdAndDayOfWeek(offerId, dayOfWeek);

        if (availabilityResults.isEmpty()){
            logger.info("No availability slots found for offer {} on day {}", offerId, dayOfWeek);
            return AvailabilityCheckResult.noAvailabilitySlots();
        }

        boolean isWithinAvailableHours = false;
        for (Object[] ob : availabilityResults){
            LocalTime slotStartTime = ((java.sql.Time) ob[2]).toLocalTime();
            LocalTime slotEndTime = ((java.sql.Time) ob[3]).toLocalTime();

            logger.info("Checking against slot: {} - {}", slotStartTime, slotEndTime);

            if (startTime.compareTo(slotStartTime) >= 0 && endTime.compareTo(slotEndTime) <= 0) {
                isWithinAvailableHours = true;
                logger.info("Time slot fits within available hours");
                break;
            }
        }

        if (!isWithinAvailableHours){
            logger.info("Requested time {} - {} is outside available hours", startTime, endTime);
            return AvailabilityCheckResult.outsideAvailableHours();
        }

        boolean isTimeSlotBooked;
        if (excludeBookingId != null) {
            isTimeSlotBooked = isTimeSlotBooked(offerId, requestedDateTime, durationMinutes, excludeBookingId);
        } else {
            isTimeSlotBooked = isTimeSlotBooked(offerId, requestedDateTime, durationMinutes);
        }

        if (isTimeSlotBooked) {
            logger.info("Time slot is already booked");
            return AvailabilityCheckResult.timeSlotAlreadyBooked();
        }

        logger.info("Time slot is available");
        return AvailabilityCheckResult.available();
    }

    public boolean isTimeSlotBooked(Long offerId, LocalDateTime startDateTime,
                                    int durationMinutes) {
        return isTimeSlotBooked(offerId, startDateTime, durationMinutes, null);
    }

    public boolean isTimeSlotBooked(Long offerId, LocalDateTime startDateTime,
                                    int durationMinutes, Long excludeBookingId) {
        LocalDateTime endDateTime = startDateTime.plusMinutes(durationMinutes);

        logger.info("Checking time slot overlap for offer {}: {} to {}, exclude booking: {}",
                offerId, startDateTime, endDateTime, excludeBookingId);

        boolean isTimeSlotBooked;

        if (excludeBookingId != null) {
            isTimeSlotBooked = bookingRepository.hasOverlappingBookings(
                    offerId, startDateTime, endDateTime, excludeBookingId);
        } else {
            isTimeSlotBooked = bookingRepository.hasOverlappingBookings(
                    offerId, startDateTime, endDateTime);
        }

        logger.info("Time slot booked result for offer {}: {}", offerId, isTimeSlotBooked);
        return isTimeSlotBooked;
    }
}
