package com.fixmate.backend.repository;

import com.fixmate.backend.entity.ProviderService;
import com.fixmate.backend.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProviderServiceRepository extends JpaRepository<ProviderService, Long> {
    boolean existsByServiceProvider_ServiceProviderIdAndService_ServiceId(
            Long providerId,
            Long serviceId
    );

    List<ProviderService> findByVerificationStatus(VerificationStatus status);

    List<ProviderService> findByServiceProvider_ServiceProviderId(Long serviceProviderId);

    List<ProviderService> findByVerificationStatusAndIsActive(
            VerificationStatus status,
            Boolean isActive
    );

    @Query("""
        SELECT ps
        FROM ProviderService ps
        JOIN ps.serviceProvider sp
        WHERE ps.verificationStatus = :status
          AND ps.isActive = true
          AND sp.isAvailable = true
          AND sp.isVerified = true
    """)
    List<ProviderService> findPublicApprovedServices(
            VerificationStatus status
    );
}
