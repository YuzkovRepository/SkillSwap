package com.example.SkillSwap.controller;

import com.example.SkillSwap.dto.*;
import com.example.SkillSwap.service.OfferAvailabilityService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/schedule")
public class OfferAvailabilityController {
    final private OfferAvailabilityService offerAvailabilityService;
    final private Logger logger = LoggerFactory.getLogger(OfferAvailabilityController.class);

    @PostMapping("/create")
    public ResponseEntity<?> createSchedule(@RequestBody @Valid OfferScheduleCreateRequestDTO request){
        logger.info("{} {}",request.offerId(),request.dayOfWeek());
        logger.info("Request to create schedule with offer ID: {}", request.offerId());
        try {
            OfferScheduleCreateUpdateResponseDTO response = offerAvailabilityService.createOfferSchedule(request);
            logger.info("Schedule with offer ID: {} successfully create", request.offerId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating schedule with service ID {}: {}", request.offerId(), e.getMessage());
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(), "Failed to create schedule of offer");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOffer(@PathVariable Long id) {
        logger.info("Request to delete a offer schedule with an ID: {}", id);
        try {
            offerAvailabilityService.deleteOfferSchedule(id);
            logger.info("Schedule with ID {} successfully deleted", id);
            SuccessResponseDTO successResponse = new SuccessResponseDTO(
                    HttpStatus.OK.value(), "Schedule successfully deleted");
            return ResponseEntity.ok(successResponse);
        } catch (Exception ex) {
            logger.error("Error deleting schedule with ID {}: {}", id, ex.getMessage());
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(), "Failed to delete schedule");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateOffer(@RequestBody @Valid OfferScheduleUpdateRequestDTO request){
        logger.info("Request to update schedule by ID: {}", request.scheduleId());
        try {
            OfferScheduleCreateUpdateResponseDTO response = offerAvailabilityService.updateOfferSchedule(request);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error update schedule with ID {}: {}", request.scheduleId(), ex.getMessage());
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(), "Failed to update schedule");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
