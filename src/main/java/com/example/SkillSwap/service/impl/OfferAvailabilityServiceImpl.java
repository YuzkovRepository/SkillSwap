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
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@AllArgsConstructor
public class OfferAvailabilityServiceImpl implements OfferAvailabilityService {
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

    public AvailabilityCheckResult checkAvailability(Long offerId, LocalDateTime requestedDateTime, int durationMinutes){
        DayOfWeek requestDayOfWeek = requestedDateTime.getDayOfWeek();
        Short dayOfWeek = (short) requestDayOfWeek.getValue();

        LocalTime startTime = requestedDateTime.toLocalTime();
        LocalTime endTime = startTime.plusMinutes(durationMinutes);

        List<Object[]> availabilityResults = availabilityRepository.findByOfferOfferIdAndDayOfWeek(offerId, dayOfWeek);
        if (availabilityResults.isEmpty()){
            return AvailabilityCheckResult.noAvailabilitySlots();
        }

        boolean isAvailability = false;
        for (Object[] ob : availabilityResults){
            LocalTime requestStartTime = ((java.sql.Time) ob[2]).toLocalTime();
            LocalTime requestEndTime = ((java.sql.Time) ob[3]).toLocalTime();

            if (!startTime.isBefore(requestStartTime) && !endTime.isAfter(requestEndTime)){
                isAvailability = true;
                break;
            }
        }

        if (!isAvailability){
            return AvailabilityCheckResult.outsideAvailableHours();
        }

        boolean isTimeSlotBooked = isTimeSlotBooked(offerId, requestedDateTime, durationMinutes);
        if (isTimeSlotBooked) {
            return AvailabilityCheckResult.timeSlotAlreadyBooked();
        }

        return AvailabilityCheckResult.available();
    }

    public boolean isTimeSlotBooked(Long offerId, LocalDateTime startDateTime, int durationMinutes) {
        LocalDateTime endDateTime = startDateTime.plusMinutes(durationMinutes);
        boolean isTimeSlotBooked = bookingRepository.hasOverlappingBookings(offerId, startDateTime, endDateTime);

        return isTimeSlotBooked;
    }
}
