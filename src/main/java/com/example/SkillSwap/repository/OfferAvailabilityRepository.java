package com.example.SkillSwap.repository;

import com.example.SkillSwap.entity.OfferAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OfferAvailabilityRepository extends JpaRepository<OfferAvailability, Long> {
    @Query(value = "select * from find_offer_by_offerid_and_dayofweek(:offerId, :dayOfWeek)",nativeQuery = true)
    List<Object[]> findByOfferOfferIdAndDayOfWeek(
            @Param("offerId") Long offerId,
            @Param("dayOfWeek") Short dayOfWeek
    );
}