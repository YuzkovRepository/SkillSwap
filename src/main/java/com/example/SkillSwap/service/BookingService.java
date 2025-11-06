package com.example.SkillSwap.service;

import com.example.SkillSwap.dto.BookingCreateRequestDTO;
import com.example.SkillSwap.dto.BookingCreateResponseDTO;

public interface BookingService {
    BookingCreateResponseDTO createBooking(BookingCreateRequestDTO bookingRequestDTO, Long customerId);
}
