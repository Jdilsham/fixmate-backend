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

    @Query("""
        SELECT b FROM Booking b
        WHERE b.bookingId = :bookingId
          AND b.serviceProvider.serviceProviderId = :serviceProviderId
    """)
        Optional<Booking> findProviderBookingForConfirm(
                @Param("bookingId") Long bookingId,
                @Param("serviceProviderId") Long serviceProviderId
        );

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("""
    SELECT b
    FROM Booking b
    WHERE b.providerService.serviceProvider.serviceProviderId = :providerId
      AND b.status IN :statuses
      AND b.scheduledAt < :endTime
      AND b.scheduledAt > :startBoundary
""")
    List<Booking> findPotentialOverlaps(
            @Param("providerId") Long providerId,
            @Param("startBoundary") Instant startBoundary,
            @Param("endTime") Instant endTime,
            @Param("statuses") List<BookingStatus> statuses
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


    // KPI counts
    long countByProviderService_ServiceProvider_ServiceProviderId(Long providerId);

    long countByProviderService_ServiceProvider_ServiceProviderIdAndStatus(
            Long providerId,
            BookingStatus status
    );

    @Query("""
        SELECT COUNT(b)
        FROM Booking b
        WHERE b.providerService.serviceProvider.serviceProviderId = :providerId
          AND b.status IN :statuses
    """)
    long countByProviderAndStatuses(
            @Param("providerId") Long providerId,
            @Param("statuses") List<BookingStatus> statuses
    );

    // Today & Upcoming bookings
    @Query("""
        SELECT b
        FROM Booking b
        WHERE b.providerService.serviceProvider.serviceProviderId = :providerId
          AND b.status NOT IN (
              com.fixmate.backend.enums.BookingStatus.REJECTED,
              com.fixmate.backend.enums.BookingStatus.CANCELLED
          )
          AND b.scheduledAt >= :from
          AND b.scheduledAt < :to
        ORDER BY b.scheduledAt ASC
    """)
    List<Booking> findProviderBookingsScheduledBetween(
            @Param("providerId") Long providerId,
            @Param("from") Instant from,
            @Param("to") Instant to
    );


    // ================= CUSTOMER DASHBOARD =================

    // Total bookings
    long countByUserId(Long userId);

    // Counts by status
    long countByUserIdAndStatus(Long userId, BookingStatus status);

        @Query("""
        SELECT COUNT(b)
        FROM Booking b
        WHERE b.user.id = :userId
          AND b.status IN :statuses
    """)
    long countByCustomerAndStatuses(
            @Param("userId") Long userId,
            @Param("statuses") List<BookingStatus> statuses
    );

    // Today & Upcoming bookings for customer
    @Query("""
    SELECT b
    FROM Booking b
    WHERE b.user.id = :userId
      AND b.status NOT IN (
          com.fixmate.backend.enums.BookingStatus.REJECTED,
          com.fixmate.backend.enums.BookingStatus.CANCELLED
      )
      AND b.scheduledAt >= :from
      AND b.scheduledAt < :to
    ORDER BY b.scheduledAt ASC
""")
    List<Booking> findCustomerBookingsScheduledBetween(
            @Param("userId") Long userId,
            @Param("from") Instant from,
            @Param("to") Instant to
    );

    // Recent bookings (last 5/10)
    List<Booking> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);
}
