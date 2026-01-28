package com.fixmate.backend.entity;

import com.fixmate.backend.enums.BookingStatus;
import com.fixmate.backend.enums.PricingType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
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

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "booking",cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_provider_id")
    private ServiceProvider serviceProvider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_service_id", nullable = false)
    private ProviderService providerService;

    @OneToOne(mappedBy = "booking",cascade = CascadeType.ALL, orphanRemoval = true)
    private Review review;

    // SNAPSHOT RELATION
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private BookingContactInfo contactInfo;

    @Enumerated(EnumType.STRING)
    @Column(name = "pricing_type", nullable = false)
    private PricingType pricingType;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;




   /* @OneToMany(mappedBy = "booking",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Address> addresses =  new HashSet<>();
*/

}
