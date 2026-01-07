package com.fixmate.backend.repository;

import com.fixmate.backend.entity.Booking;
import com.fixmate.backend.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // 🔹 Provider booking list
    List<Booking> findByServiceProvider_ServiceProviderId(Long serviceProviderId);

    List<Booking> findByServiceProvider_ServiceProviderIdAndStatus(
            Long serviceProviderId,
            BookingStatus status
    );

    Optional<Booking> findByBookingIdAndServiceProvider_ServiceProviderId(
            Long bookingId,
            Long serviceProviderId
    );

    // 🔹 Provider earnings (CONFIRMED payments only)
    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE p.provider.serviceProviderId = :providerId
          AND p.status = com.fixmate.backend.enums.PaymentStatus.CONFIRMED
    """)
    BigDecimal sumConfirmedAmounts(Long providerId);
}
