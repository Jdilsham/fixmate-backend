package com.fixmate.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long payementId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "payment_method",nullable = false)
    private String paymentMethod;

    @Column(name = "transaction_ref")
    private String transactionRef;

    private String status;

    @Column(name = "paid_at")
    private Instant paidAt;


    @Column(name = "created_at")
    private Instant createdAt = Instant.now();


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;



}
