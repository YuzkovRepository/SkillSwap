package com.example.SkillSwap.controller;

import com.example.SkillSwap.dto.SkillCreateRequestDTO;
import com.example.SkillSwap.dto.SkillCreateResponseDTO;
import com.example.SkillSwap.dto.SuccessResponseDTO;
import com.example.SkillSwap.dto.ErrorResponseDTO;
import com.example.SkillSwap.service.SkillService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1/skills")
public class SkillController {
    private static final Logger logger = LoggerFactory.getLogger(SkillController.class);
    final private SkillService skillService;

    @PostMapping("/create")
    public ResponseEntity<?> addSkill(@RequestBody @Valid SkillCreateRequestDTO request) {
        logger.info("Creating skill: {}", request.name());
        try {
            SkillCreateResponseDTO response = skillService.addSkill(request);
            logger.info("Skill with name {} successfully create", request.name());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception ex) {
            logger.error("Error creating skill with name {}: {}", request.name(), ex.getMessage());
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(), "Failed to create skill");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteSkill(@PathVariable Long id) {
        logger.info("Request to delete a skill with an ID: {}", id);
        try {
            skillService.deleteSkill(id);
            logger.info("Skill with ID {} successfully deleted", id);
            SuccessResponseDTO successResponse = new SuccessResponseDTO(
                    HttpStatus.OK.value(), "Skill successfully deleted");
            return ResponseEntity.ok(successResponse);
        } catch (Exception ex) {
            logger.error("Error deleting skill with ID {}: {}", id, ex.getMessage());
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(), "Failed to delete skill");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
