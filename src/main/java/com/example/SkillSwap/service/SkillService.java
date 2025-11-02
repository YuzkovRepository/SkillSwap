package com.example.SkillSwap.service;

import com.example.SkillSwap.dto.SkillCreateRequestDTO;
import com.example.SkillSwap.dto.SkillCreateResponseDTO;

public interface SkillService {
    SkillCreateResponseDTO addSkill(SkillCreateRequestDTO skillCreateRequest);
    void deleteSkill(Long id);
}
