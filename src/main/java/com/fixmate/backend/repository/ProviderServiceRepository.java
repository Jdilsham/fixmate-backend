package com.fixmate.backend.repository;

import com.fixmate.backend.entity.ProviderService;
import com.fixmate.backend.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

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

}
