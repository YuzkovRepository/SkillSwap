package com.example.SkillSwap.service.impl;

import com.example.SkillSwap.entity.Booking;
import com.example.SkillSwap.entity.Transaction;
import com.example.SkillSwap.entity.User;
import com.example.SkillSwap.repository.TransactionRepository;
import com.example.SkillSwap.repository.UserRepository;
import com.example.SkillSwap.exception.CommonException;
import com.example.SkillSwap.service.NotificationService;
import com.example.SkillSwap.service.TransactionService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public void reservePayment(Booking booking) {
        User customer = booking.getUser();
        User provider = booking.getOffer().getUser();
        int amount = booking.getTotalPrice();

        if (customer.getBalance() < amount) {
            throw new CommonException("Недостаточно средств на балансе");
        }

        customer.setBalance(customer.getBalance() - amount);
        userRepository.save(customer);

        Transaction reservation = new Transaction();
        reservation.setFromUser(customer);
        reservation.setToUser(provider);
        reservation.setBooking(booking);
        reservation.setAmount(amount);
        reservation.setTransactionType(Transaction.TransactionType.SERVICE_PAYMENT);

        transactionRepository.save(reservation);

        logger.info("Payment reserved: {} from user {} to user {} for booking {}",
                amount, customer.getUserId(), provider.getUserId(), booking.getBookingId());

        notifyPaymentReserved(customer, provider, amount, booking);
    }

    public void completePayment(Booking booking) {
        User customer = booking.getUser();
        User provider = booking.getOffer().getUser();
        int amount = booking.getTotalPrice();

        provider.setBalance(provider.getBalance() + amount);
        userRepository.save(provider);

        logger.info("Payment completed: {} transferred to user {} for booking {}",
                amount, provider.getUserId(), booking.getBookingId());

        notifyPaymentCompleted(customer, provider, amount, booking);
    }

    public void refundPayment(Booking booking) {
        User customer = booking.getUser();
        User provider = booking.getOffer().getUser();
        int amount = booking.getTotalPrice();

        customer.setBalance(customer.getBalance() + amount);
        userRepository.save(customer);

        Transaction refund = new Transaction();
        refund.setFromUser(provider);
        refund.setToUser(customer);
        refund.setBooking(booking);
        refund.setAmount(amount);
        refund.setTransactionType(Transaction.TransactionType.REFUND);

        transactionRepository.save(refund);

        logger.info("Payment refunded: {} returned to user {} for booking {}",
                amount, customer.getUserId(), booking.getBookingId());

        notifyPaymentRefunded(customer, amount, booking);
    }

    public void partialRefundWithPenalty(Booking booking, int penaltyAmount) {
        User customer = booking.getUser();
        User provider = booking.getOffer().getUser();
        int totalAmount = booking.getTotalPrice();
        int refundAmount = totalAmount - penaltyAmount;

        customer.setBalance(customer.getBalance() + refundAmount);
        provider.setBalance(provider.getBalance() + penaltyAmount);

        userRepository.save(customer);
        userRepository.save(provider);

        Transaction refund = new Transaction();
        refund.setFromUser(provider);
        refund.setToUser(customer);
        refund.setBooking(booking);
        refund.setAmount(refundAmount);
        refund.setTransactionType(Transaction.TransactionType.REFUND);

        Transaction penalty = new Transaction();
        penalty.setFromUser(customer);
        penalty.setToUser(provider);
        penalty.setBooking(booking);
        penalty.setAmount(penaltyAmount);
        penalty.setTransactionType(Transaction.TransactionType.PENALTY);

        transactionRepository.save(refund);
        transactionRepository.save(penalty);

        logger.info("Partial refund: {} returned to user {}, penalty {} to user {}",
                refundAmount, customer.getUserId(), penaltyAmount, provider.getUserId());

        notifyPartialRefund(customer, provider, refundAmount, penaltyAmount, booking);
    }

    private void notifyPaymentReserved(User customer, User provider, int amount, Booking booking) {
        String customerMessage = String.format(
                "Сумма %d руб. заблокирована на вашем счете\n\nУслуга: %s\n" +
                        "Будет списана после завершения занятия",
                amount, booking.getOffer().getTitle()
        );

        String providerMessage = String.format(
                "Сумма %d руб. заблокирована у клиента\n\nУслуга: %s\n" +
                        "Будет зачислена после завершения занятия",
                amount, booking.getOffer().getTitle()
        );

        notificationService.sendNotification(customer.getUserId(),
                NotificationServiceImpl.NotificationType.PAYMENT_RESERVED, "Средства заблокированы", customerMessage, booking);
        notificationService.sendNotification(provider.getUserId(),
                NotificationServiceImpl.NotificationType.PAYMENT_RESERVED, "Ожидание оплаты", providerMessage, booking);
    }

    private void notifyPaymentCompleted(User customer, User provider, int amount, Booking booking) {
        String customerMessage = String.format(
                "Сумма %d руб. списана за услугу\n\n%s\n" +
                        "Оплата завершена",
                amount, booking.getOffer().getTitle()
        );

        String providerMessage = String.format(
                "Сумма %d руб. зачислена на ваш счет\n\n%s\n" +
                        "Оплата получена",
                amount, booking.getOffer().getTitle()
        );

        notificationService.sendNotification(customer.getUserId(),
                NotificationServiceImpl.NotificationType.PAYMENT_COMPLETED, "Оплата списана", customerMessage, booking);
        notificationService.sendNotification(provider.getUserId(),
                NotificationServiceImpl.NotificationType.PAYMENT_COMPLETED, "Оплата получена", providerMessage, booking);
    }

    private void notifyPaymentRefunded(User customer, int amount, Booking booking) {
        String message = String.format(
                "Сумма %d руб. возвращена на ваш счет\n\n%s\n" +
                        "↩Возврат средств",
                amount, booking.getOffer().getTitle()
        );

        notificationService.sendNotification(customer.getUserId(),
                NotificationServiceImpl.NotificationType.PAYMENT_REFUNDED, "Возврат средств", message, booking);
    }

    private void notifyPartialRefund(User customer, User provider, int refundAmount, int penaltyAmount, Booking booking) {
        String customerMessage = String.format(
                "Частичный возврат: %d руб.\nШтраф: %d руб.\n\n%s\n" +
                        "⚠Часть суммы удержана как штраф",
                refundAmount, penaltyAmount, booking.getOffer().getTitle()
        );

        String providerMessage = String.format(
                "Получен штраф: %d руб.\n\n%s\n" +
                        "Компенсация за отмену",
                penaltyAmount, booking.getOffer().getTitle()
        );

        notificationService.sendNotification(customer.getUserId(),
                NotificationServiceImpl.NotificationType.PARTIAL_REFUND, "Частичный возврат", customerMessage, booking);
        notificationService.sendNotification(provider.getUserId(),
                NotificationServiceImpl.NotificationType.PENALTY_RECEIVED, "Получен штраф", providerMessage, booking);
    }
}
