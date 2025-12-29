package com.fixmate.backend.repository;

import com.fixmate.backend.entity.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {

    Optional<ServiceProvider> findByUserEmail(String email);

    List<ServiceProvider> findByServicesCategoryName(String categoryName);

    @Query("""
        SELECT sp FROM ServiceProvider sp
        JOIN sp.services s
        WHERE s.serviceId = :serviceId
          AND sp.isAvailable = true
          AND sp.isVerified = true
    """)
    List<ServiceProvider> findAvailableVerifiedByServiceId(Long serviceId);

    List<ServiceProvider> findByIsVerifiedFalse();

    Optional<ServiceProvider> findByUserId(Long userId);
}
