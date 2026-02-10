package com.fixmate.backend.repository;

import com.fixmate.backend.entity.Booking;
import com.fixmate.backend.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // 🔹 Provider booking list
    List<Booking> findByProviderService_ServiceProvider_ServiceProviderId(Long serviceProviderId);

//    List<Booking> findByServiceProvider_ServiceProviderIdAndStatus(
//            Long serviceProviderId,
//            BookingStatus status
//    );

    @Query("""
        SELECT b FROM Booking b
        WHERE b.bookingId = :bookingId
          AND b.providerService.id = :providerServiceId
          AND b.providerService.serviceProvider.serviceProviderId = :serviceProviderId
    """)

    Optional<Booking> findProviderBookingById
            (
                    @Param("bookingId") Long bookingId,
                    @Param("providerServiceId") Long providerServiceId,
                    @Param("serviceProviderId") Long serviceProviderId
    );

    // 🔹 Provider earnings (CONFIRMED payments only)
//    @Query("""
//        SELECT COALESCE(SUM(p.amount), 0)
//        FROM Payment p
//        WHERE p.provider.serviceProviderId = :providerId
//          AND p.status = com.fixmate.backend.enums.PaymentStatus.CONFIRMED
//    """)
//    BigDecimal sumConfirmedAmounts(Long providerId);

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("""
        SELECT b
        FROM Booking b
        WHERE b.providerService.serviceProvider.serviceProviderId = :providerId
          AND b.status IN (
            com.fixmate.backend.enums.BookingStatus.PENDING,
            com.fixmate.backend.enums.BookingStatus.ACCEPTED,
            com.fixmate.backend.enums.BookingStatus.IN_PROGRESS
          )
          AND b.scheduledAt < :endTime
          AND b.scheduledAt > :startBoundary
    """)
    List<Booking> findPotentialOverlaps(
            @Param("providerId") Long providerId,
            @Param("startBoundary") Instant startBoundary,
            @Param("endTime") Instant endTime
    );

    @Query("""
      SELECT b.scheduledAt
      FROM Booking b
      WHERE b.providerService.id = :providerServiceId
        AND b.status IN (
          com.fixmate.backend.enums.BookingStatus.PENDING,
          com.fixmate.backend.enums.BookingStatus.ACCEPTED,
          com.fixmate.backend.enums.BookingStatus.IN_PROGRESS
        )
        AND b.scheduledAt >= :dayStart
        AND b.scheduledAt < :dayEnd
    """)
        List<Instant> findBookedSlotsForDay(
                Long providerServiceId,
                Instant dayStart,
                Instant dayEnd
        );

    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE (:providerId IS NULL OR p.provider.serviceProviderId = :providerId)
          AND p.status = com.fixmate.backend.enums.PaymentStatus.CONFIRMED
    """)
    BigDecimal sumConfirmedAmounts(@Param("providerId") Long providerId);

}
