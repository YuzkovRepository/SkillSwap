package com.example.SkillSwap.service.impl;

import com.example.SkillSwap.dto.OfferingCreateRequestDTO;
import com.example.SkillSwap.dto.OfferingResponseDTO;
import com.example.SkillSwap.dto.OfferingUpdateRequestDTO;
import com.example.SkillSwap.entity.Offer;
import com.example.SkillSwap.entity.Skill;
import com.example.SkillSwap.entity.User;
import com.example.SkillSwap.exception.CommonException;
import com.example.SkillSwap.repository.OfferAvailabilityRepository;
import com.example.SkillSwap.repository.OfferRepository;
import com.example.SkillSwap.repository.SkillRepository;
import com.example.SkillSwap.repository.UserRepository;
import com.example.SkillSwap.service.OfferService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OfferServiceImpl implements OfferService {
    final private OfferRepository offerRepository;
    final private SkillRepository skillRepository;
    final private UserRepository userRepository;
    final private OfferAvailabilityRepository offerAvailabilityRepository;

    @Override
    @Transactional
    public void createOffer(OfferingCreateRequestDTO request) {
        Skill skill = skillRepository.findById(request.skillId())
                .orElseThrow(() -> new CommonException("Skill not found"));
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new CommonException("User not found"));
        Offer offer = new Offer();
        offer.setSkill(skill);
        offer.setUser(user);
        offer.setTitle(request.title());
        offer.setDescription(request.description());
        if (request.address() != null){
            offer.setAddress(request.address());
        }
        offer.setPrice(request.price());
        offer.setDurationMinutes(request.durationMinutes());

        Offer.OfferType offerType;
        if (request.typeOffer() != null && !request.typeOffer().trim().isEmpty()) {
            try {
                offerType = Offer.OfferType.valueOf(request.typeOffer().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new CommonException("Invalid offer type: " + request.typeOffer());
            }
        } else {
            offerType = Offer.OfferType.RECURRING;
        }
        offer.setOfferType(offerType);
        offerRepository.save(offer);

    }

    @Override
    @Transactional
    public void deleteOffer(Long id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new CommonException("Offer not found with id: " + id));

        offer.clearAvailabilities();
        offerRepository.delete(offer);
    }

    @Override
    public List<OfferingResponseDTO> getOffersByUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CommonException("User no found"));

        List<Offer> offers = offerRepository.findOfferByUser(user);
        return offers.stream().map(this::mapOfferToDTO).toList();
    }

    @Override
    public List<OfferingResponseDTO> getOffers() {
        return offerRepository.findAll().stream().map(this::mapOfferToDTO).toList();
    }

    @Override
    public OfferingResponseDTO updateOffer(OfferingUpdateRequestDTO request) {
        Offer offer = offerRepository.findById(request.offerId())
                .orElseThrow(() -> new CommonException("Offer not found"));
        offer.setTitle(request.title());
        offer.setDescription(request.description());
        offer.setPrice(request.price());
        offer.setDurationMinutes(request.durationMinutes());
        if (request.address() != null){
            offer.setAddress(request.address());
        }
        offerRepository.save(offer);

        return mapOfferToDTO(offer);
    }

    private OfferingResponseDTO mapOfferToDTO(Offer offer){
        String address = (offer.getAddress() != null) ?
                offer.getAddress() : null;

        return new OfferingResponseDTO(
                offer.getTitle(),
                offer.getDescription(),
                offer.getSkill().getName(),
                offer.getPrice(),
                offer.getDurationMinutes(),
                offer.getMaxParticipants(),
                offer.getOfferType().toString(),
                offer.getStatus().toString(),
                address);
    }
}
