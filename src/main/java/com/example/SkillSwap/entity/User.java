package com.example.SkillSwap.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long userId;

    @Column(nullable = false, length = 20, unique = true)
    String login;
    @Column(nullable = false, length = 255)
    String password_hash;
    @Column(nullable = false, length = 100, unique = true)
    String email;

    @Column(nullable = false, length = 60)
    String firstName;
    @Column(nullable = false, length = 80)
    String lastName;
    @Column(nullable = false, length = 80)
    String surname;

    @Column(nullable = false, length = 15)
    String phone;

    String avatarUrl;
    String bio;
    int balance;
    int level;

    @Digits(integer = 1, fraction = 1)
    @Column(precision = 2, scale = 1)
    private BigDecimal trustScore;

    @Column(nullable = false)
    LocalDateTime createdAt;
    @Column(nullable = false)
    LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        balance = 0;
        level = 0;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }


    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<UserRole> userRoles = new HashSet<>();

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Review> authoredReviews = new HashSet<>();

    @OneToMany(mappedBy = "targetUser", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Review> receivedReviews = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Service> services = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Booking> bookings = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<UserSkill> userSkills = new HashSet<>();

    @OneToMany(mappedBy = "fromUser", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Transaction> fromTransactions = new HashSet<>();

    @OneToMany(mappedBy = "toUser", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Transaction> toTransactions = new HashSet<>();

    @OneToMany(mappedBy = "initiator", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Dispute> initiatorDisputes = new HashSet<>();

    @OneToMany(mappedBy = "resolvedBy", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Dispute> resolvedByDisputes = new HashSet<>();
}