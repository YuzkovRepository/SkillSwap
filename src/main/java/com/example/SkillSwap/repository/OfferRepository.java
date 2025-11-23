package com.example.SkillSwap.repository;

import com.example.SkillSwap.dto.ServiceSearchResponseDTO;
import com.example.SkillSwap.entity.Offer;
import com.example.SkillSwap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    boolean existsByOfferAvailabilities(Offer offer);
    List<Offer> findOfferByUser(User user);


    @Query(value = "SELECT * FROM search_services_by_skill(:skillName, :minPrice, :maxPrice, :minRating, :customerId)",
            nativeQuery = true)
    List<Object[]>  searchServicesBySkill(
            @Param("skillName") String skillName,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("minRating") BigDecimal minRating,
            @Param("customerId") Long customerId
    );
}
