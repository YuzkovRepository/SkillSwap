package com.example.SkillSwap.repository;

import com.example.SkillSwap.entity.OfferAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferAvailabilityRepository extends JpaRepository<OfferAvailability, Long> {
    boolean existsByOffer_OfferId(Long offerId);
    List<OfferAvailability> findByOffer_OfferId(Long offerId);
}
