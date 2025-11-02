package com.example.SkillSwap.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "disputes")
public class Dispute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long disputeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    User initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    Booking booking;

    @Column(length = 100)
    String reason;

    @Column(columnDefinition = "TEXT")
    String description;

    Status status;

    String resolution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_id", nullable = false)
    User resolvedBy;

    LocalDateTime resolvedAt;

    @Column(nullable = false)
    LocalDateTime createdAt;

    public enum Status{
        OPEN,
        IN_REVIEW,
        RESOLVED,
        CANCELLED
    }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        status = Status.OPEN;
    }
}
