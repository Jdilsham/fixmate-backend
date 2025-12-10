package com.fixmate.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "booking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "scheduled_at")
    private Instant scheduledAt;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "booking",cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_provider_id")
    private ServiceProvider serviceProvider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private Service service;

    @OneToOne(mappedBy = "booking",cascade = CascadeType.ALL, orphanRemoval = true)
    private Review review;

    @OneToMany(mappedBy = "booking",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Address> addresses =  new HashSet<>();


}
