package com.example.SkillSwap.service.impl;

import com.example.SkillSwap.dto.SkillCreateRequestDTO;
import com.example.SkillSwap.dto.SkillCreateResponseDTO;
import com.example.SkillSwap.entity.Skill;
import com.example.SkillSwap.exception.CommonException;
import com.example.SkillSwap.repository.SkillRepository;
import com.example.SkillSwap.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    final private SkillRepository skillRepository;

    @Override
    public SkillCreateResponseDTO addSkill(SkillCreateRequestDTO request) {
        if (skillRepository.existsByName(request.name())){
            throw new CommonException("Skill '" + request.name() + "' already exists");
        }
        Skill skill = new Skill();
        skill.setName(request.name());
        skill.setDescription(request.description());
        if (request.parentSkillId() != null){
            Skill skillParent = skillRepository.findById(request.parentSkillId())
                    .orElseThrow(() -> new CommonException("Parent skill not found"));
            skill.setParentSkill(skillParent);
        }

        Skill savedSkill = skillRepository.save(skill);

        return mapSkillToDTO(savedSkill);
    }

    @Override
    public void deleteSkill(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new CommonException("Skill not found with id: " + id));

        if (skillRepository.existsByParentSkill(skill)) {
            throw new CommonException("Cannot delete skill with child skills");
        }

        skillRepository.delete(skill);
    }

    private SkillCreateResponseDTO mapSkillToDTO(Skill skill){
        Long parentSkillId = (skill.getParentSkill() != null)
                ? skill.getParentSkill().getSkillId()
                : null;

        return new SkillCreateResponseDTO(
                skill.getName(),
                skill.getDescription(),
                parentSkillId);
    }
}
