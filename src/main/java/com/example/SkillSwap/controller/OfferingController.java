package com.example.SkillSwap.controller;

import com.example.SkillSwap.dto.*;
import com.example.SkillSwap.security.CustomUserDetails;
import com.example.SkillSwap.service.OfferService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/offers")
public class OfferingController {
    private static final Logger logger = LoggerFactory.getLogger(OfferingController.class);
    final private OfferService offerService;

    @PostMapping("/create")
    public ResponseEntity<?> addOffer(@RequestBody @Valid OfferingCreateRequestDTO request){
        try{
            offerService.createOffer(request);
            logger.info("Offer with title {} successfully create", request.title());
            SuccessResponseDTO successResponseDTO = new SuccessResponseDTO(HttpStatus.CREATED.value(), "Offer successfully create");
            return ResponseEntity.ok(successResponseDTO);
        } catch (Exception ex){
            logger.error("Error creating offer with title {}: {}", request.title(), ex.getMessage());
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(), "Failed to create offer");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOffer(@PathVariable Long id) {
        logger.info("Request to delete a offer with an ID: {}", id);
        try {
            offerService.deleteOffer(id);
            logger.info("Offer with ID {} successfully deleted", id);
            SuccessResponseDTO successResponse = new SuccessResponseDTO(
                    HttpStatus.OK.value(), "Offer successfully deleted");
            return ResponseEntity.ok(successResponse);
        } catch (Exception ex) {
            logger.error("Error deleting offer with ID {}: {}", id, ex.getMessage());
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(), "Failed to delete offer");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping
    public ResponseEntity<?> getOffers(){
        logger.info("Request to get all offers");
        try {
            List<OfferingResponseDTO> response = offerService.getOffers();
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error getting offers: {}", ex.getMessage());
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(), "Failed to getting all offers");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getOffersByUser(@PathVariable Long id){
        logger.info("Request to get offers by user ID: {}", id);
        try {
            List<OfferingResponseDTO> response = offerService.getOffersByUser(id);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error find offers with user ID {}: {}", id, ex.getMessage());
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(), "Failed to find offers of user");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateOffer(@RequestBody @Valid OfferingUpdateRequestDTO request){
        logger.info("Request to update offer by ID: {}", request.offerId());
        try {
            OfferingResponseDTO response = offerService.updateOffer(request);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error update offer with ID {}: {}", request.offerId(), ex.getMessage());
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(), "Failed to update offer");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PreAuthorize("hasAuthority('SEARCH_SERVICE')")
    @GetMapping("/search")
    public ResponseEntity<List<ServiceSearchResponseDTO>> searchServices(
            @RequestParam String skill,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) BigDecimal minRating,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long customerId = extractUserId(userDetails);

        List<ServiceSearchResponseDTO> results = offerService.searchServicesBySkill(
                skill, minPrice, maxPrice, minRating, customerId
        );

        return ResponseEntity.ok(results);
    }

    private Long extractUserId(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails customUser) {
            return customUser.getId();
        }
        return null;
    }
}
