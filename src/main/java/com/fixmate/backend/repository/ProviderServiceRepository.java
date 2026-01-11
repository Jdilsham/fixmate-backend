package com.fixmate.backend.repository;

import com.fixmate.backend.entity.ProviderService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderServiceRepository extends JpaRepository<ProviderService, Long> {
    boolean existsByServiceProvider_ServiceProviderIdAndService_ServiceId(
            Long providerId,
            Long serviceId
    );
}
