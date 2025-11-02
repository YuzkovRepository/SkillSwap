package com.example.SkillSwap.repository;

import com.example.SkillSwap.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferingRepository extends JpaRepository<Service, Long> {
}
