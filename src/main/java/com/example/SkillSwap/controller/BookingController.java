package com.example.SkillSwap.controller;

import com.example.SkillSwap.dto.*;
import com.example.SkillSwap.entity.Booking;
import com.example.SkillSwap.entity.User;
import com.example.SkillSwap.exception.CommonException;
import com.example.SkillSwap.repository.UserRepository;
import com.example.SkillSwap.security.CustomUserDetails;
import com.example.SkillSwap.service.BookingService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/bookings")
public class BookingController {
    final private Logger logger = LoggerFactory.getLogger(BookingController.class);
    final private BookingService bookingService;
    final private UserRepository userRepository;

    @PreAuthorize("hasAuthority('SEARCH_SERVICE')")
    @PostMapping("/create")
    public ResponseEntity<?> createBooking(
            @RequestBody @Valid BookingCreateRequestDTO request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new CommonException("User not found"));
        Long customerId = user.getUserId();

        logger.info("Request to create booking with customer ID {} and offer ID {}", customerId, request.offerId());

        BookingCreateResponseDTO response = bookingService.createBooking(request, customerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{bookingId}/confirm")
    public ResponseEntity<?> confirmBooking(@PathVariable Long bookingId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new CommonException("User not found"));
        Long providerId = user.getUserId();

        try {
            BookingCreateResponseDTO response = bookingService.confirmBooking(bookingId, providerId);
            return ResponseEntity.ok(new SuccessResponseWithModelDTO(
                    HttpStatus.OK.value(),
                    "Бронирование подтверждено",
                    response
            ));
        } catch (CommonException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage()
            ));
        }
    }

    @PostMapping("/{bookingId}/reject")
    public ResponseEntity<?> rejectBooking(
            @PathVariable Long bookingId,
            @RequestBody(required = false) BookingRejectRequestDTO rejectRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new CommonException("User not found"));
        Long providerId = user.getUserId();

        try {
            BookingCreateResponseDTO response = bookingService.rejectBooking(
                    bookingId,
                    providerId,
                    rejectRequest != null ? rejectRequest.reason() : null
            );

            return ResponseEntity.ok(new SuccessResponseWithModelDTO(
                    HttpStatus.OK.value(),
                    "Бронирование отклонено",
                    response
            ));
        } catch (CommonException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    e.getMessage()
            ));
        }
    }
}
