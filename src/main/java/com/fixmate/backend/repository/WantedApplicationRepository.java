package com.fixmate.backend.repository;

import com.fixmate.backend.entity.WantedApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WantedApplicationRepository extends JpaRepository<WantedApplication, Long> {
    boolean existsByWantedPostIdAndServiceProviderServiceProviderId(Long postId, Long providerId);
}