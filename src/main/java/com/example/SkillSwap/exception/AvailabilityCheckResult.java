package com.example.SkillSwap.exception;

public class AvailabilityCheckResult {
    private final boolean available;
    private final AvailabilityStatus status;
    private final String message;

    public AvailabilityCheckResult(boolean available, AvailabilityStatus status, String message) {
        this.available = available;
        this.status = status;
        this.message = message;
    }

    // геттеры
    public boolean isAvailable() { return available; }
    public AvailabilityStatus getStatus() { return status; }
    public String getMessage() { return message; }

    // Статические методы-конструкторы
    public static AvailabilityCheckResult available() {
        return new AvailabilityCheckResult(true, AvailabilityStatus.AVAILABLE, "Time slot is available");
    }

    public static AvailabilityCheckResult noAvailabilitySlots() {
        return new AvailabilityCheckResult(false, AvailabilityStatus.NO_AVAILABILITY_SLOTS,
                "No availability slots found for this day");
    }

    public static AvailabilityCheckResult outsideAvailableHours() {
        return new AvailabilityCheckResult(false, AvailabilityStatus.OUTSIDE_AVAILABLE_HOURS,
                "Selected time is outside available hours");
    }

    public static AvailabilityCheckResult timeSlotAlreadyBooked() {
        return new AvailabilityCheckResult(false, AvailabilityStatus.TIME_SLOT_ALREADY_BOOKED,
                "This time slot is already booked");
    }

    public enum AvailabilityStatus {
        AVAILABLE,
        NO_AVAILABILITY_SLOTS,
        OUTSIDE_AVAILABLE_HOURS,
        TIME_SLOT_ALREADY_BOOKED
    }
}
