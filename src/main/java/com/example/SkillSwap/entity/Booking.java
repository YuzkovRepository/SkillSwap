package com.example.SkillSwap.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookings", indexes = {
        @Index(name = "idx_booking_user", columnList = "user_id"),
        @Index(name = "idx_booking_offer", columnList = "offer_id"),
        @Index(name = "idx_booking_status", columnList = "status")
})
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", nullable = false)
    Offer offer;

    @Enumerated(EnumType.STRING)
    Status status;

    @Column(name = "meeting_url", length = 500)
    private String meetingUrl;

    @Column(name = "meeting_id")
    private String meetingId;

    LocalDateTime scheduledDatetime;

    @Min(0)
    int durationMinutes;

    @Min(0)
    int totalPrice;

    @Column(columnDefinition = "TEXT")
    String customerNotes;

    @Column(nullable = false)
    LocalDateTime createdAt;
    @Column(nullable = false)
    LocalDateTime updatedAt;


    public enum Status {
        PENDING,
        CONFIRMED,
        IN_PROCESS,
        COMPLETED,
        CANCELLED,
        DISPUTED
    }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        status = Status.PENDING;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Transaction> transactions = new HashSet<>();

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Dispute> disputes = new HashSet<>();
}
