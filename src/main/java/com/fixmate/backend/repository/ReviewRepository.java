package com.fixmate.backend.repository;

import com.fixmate.backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByServiceProvider_ServiceProviderId(Long serviceProviderId);

    @Query("""
        SELECT AVG(r.rating)
        FROM Review r
        WHERE r.serviceProvider.serviceProviderId = :providerId
    """)

    BigDecimal calculateAvgRating(@Param("providerId") Long providerId);

    @Query("""
        SELECT r.booking.bookingId
        FROM Review r
        WHERE r.user.id = :userId
    """)
    List<Long> findReviewedBookingIdsByUser(@Param("userId") Long userId);
}
