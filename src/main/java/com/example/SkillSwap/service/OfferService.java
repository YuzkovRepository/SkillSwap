package com.example.SkillSwap.service;

import com.example.SkillSwap.dto.OfferingCreateRequestDTO;
import com.example.SkillSwap.dto.OfferingResponseDTO;
import com.example.SkillSwap.dto.OfferingUpdateRequestDTO;

import java.util.List;

public interface OfferService {
    void createOffer(OfferingCreateRequestDTO request);
    void deleteOffer(Long id);
    List<OfferingResponseDTO> getOffersByUser(Long id);
    List<OfferingResponseDTO> getOffers();
    OfferingResponseDTO updateOffer(OfferingUpdateRequestDTO request);
}
