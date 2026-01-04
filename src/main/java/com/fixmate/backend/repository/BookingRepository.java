package com.fixmate.backend.repository;

import com.fixmate.backend.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // 🔹 Provider booking list
    List<Booking> findByServiceProvider_ServiceProviderId(Long serviceProviderId);

    // 🔹 Provider earnings (PAID payments only)
    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE p.booking.serviceProvider.serviceProviderId = :providerId
          AND p.status = 'PAID'
    """)
    BigDecimal sumPaidAmounts(Long providerId);
}
