package com.fixmate.backend.repository;

import com.fixmate.backend.entity.Payment;
import com.fixmate.backend.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByBooking_BookingId(Long bookingId);

    Optional<Payment> findByTransactionRef(String transactionRef);

    // Lifetime income
    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE p.provider.serviceProviderId = :providerId
          AND p.status = :status
    """)
    BigDecimal sumByProviderAndStatus(
            @Param("providerId") Long providerId,
            @Param("status") PaymentStatus status
    );

    // Income between dates
    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE p.provider.serviceProviderId = :providerId
          AND p.status = :status
          AND p.paidAt >= :from
          AND p.paidAt < :to
    """)
    BigDecimal sumByProviderAndStatusAndPaidAtBetween(
            @Param("providerId") Long providerId,
            @Param("status") PaymentStatus status,
            @Param("from") Instant from,
            @Param("to") Instant to
    );

    //Chart data: all confirmed payments in a range
    @Query("""
        SELECT p
        FROM Payment p
        WHERE p.provider.serviceProviderId = :providerId
          AND p.status = :status
          AND p.paidAt IS NOT NULL
          AND p.paidAt >= :from
          AND p.paidAt < :to
        ORDER BY p.paidAt ASC
    """)
    List<Payment> findByProviderAndStatusAndPaidAtBetween(
            @Param("providerId") Long providerId,
            @Param("status") PaymentStatus status,
            @Param("from") Instant from,
            @Param("to") Instant to
    );
}