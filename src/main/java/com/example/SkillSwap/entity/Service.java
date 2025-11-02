package com.example.SkillSwap.entity;

import jakarta.persistence.*;
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
@Table(name = "services", indexes = {
        @Index(name = "idx_service_user", columnList = "user_id"),
        @Index(name = "idx_service_skill", columnList = "skill_id")
})
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long serviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    String title;

    @Column(columnDefinition = "TEXT")
    String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    Skill skill;

    int price;

    int durationMinutes;

    int maxParticipants;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    private Status status;

    String address;

    public enum ServiceType {
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
        if (serviceType == null) {
            serviceType = ServiceType.RECURRING;
        }
        if (status == null) {
            status = Status.ACTIVE;
        }
    }

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Booking> bookings = new HashSet<>();

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ServiceAvailability> serviceAvailabilities = new HashSet<>();
}
