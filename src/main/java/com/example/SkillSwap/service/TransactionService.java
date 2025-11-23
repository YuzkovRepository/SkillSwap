package com.example.SkillSwap.service;

import com.example.SkillSwap.entity.Booking;

public interface TransactionService {
    void reservePayment(Booking booking);
    void completePayment(Booking booking);
    void refundPayment(Booking booking);
    void partialRefundWithPenalty(Booking booking, int penaltyAmount);
}
