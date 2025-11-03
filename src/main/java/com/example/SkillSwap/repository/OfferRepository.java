package com.example.SkillSwap.repository;

import com.example.SkillSwap.entity.Offer;
import com.example.SkillSwap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    boolean existsByOfferAvailabilities(Offer offer);
    List<Offer> findOfferByUser(User user);
}
