package com.example.SkillSwap.service;


import com.example.SkillSwap.dto.OfferScheduleCreateRequestDTO;
import com.example.SkillSwap.dto.OfferScheduleCreateUpdateResponseDTO;
import com.example.SkillSwap.dto.OfferScheduleUpdateRequestDTO;

public interface OfferAvailabilityService {
    OfferScheduleCreateUpdateResponseDTO createOfferSchedule(OfferScheduleCreateRequestDTO request);
    void deleteOfferSchedule(Long id);
    OfferScheduleCreateUpdateResponseDTO updateOfferSchedule(OfferScheduleUpdateRequestDTO request);
}
