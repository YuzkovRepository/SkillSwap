package com.example.SkillSwap.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;


@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "offers", indexes = {
        @Index(name = "idx_offer_user", columnList = "user_id"),
        @Index(name = "idx_offer_skill", columnList = "skill_id")
})
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long offerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @Column(length = 100)
    String title;

    @Column(columnDefinition = "TEXT")
    String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    Skill skill;

    @Min(1)
    int price;

    @Min(1)
    int durationMinutes;

    @Min(1)
    int maxParticipants = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private OfferType offerType;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(columnDefinition = "TEXT")
    String address;

    public enum OfferType {
        ONE_TIME,
        RECURRING,
        GROUP
    }

    public enum Status {
        ACTIVE,
        INACTIVE,
        SUSPEND
    }

    @PrePersist
    protected void onCreate() {
        if (offerType == null) {
            offerType = OfferType.RECURRING;
        }
        if (status == null) {
            status = Status.ACTIVE;
        }
    }

    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Booking> bookings = new HashSet<>();

    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<OfferAvailability> offerAvailabilities = new HashSet<>();

    public void clearAvailabilities() {
        this.offerAvailabilities.clear();
    }
}
