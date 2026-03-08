package com.fixmate.backend.repository;

import com.fixmate.backend.entity.WantedApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WantedApplicationRepository
        extends JpaRepository<WantedApplication, Long> {

    boolean existsByWantedPost_IdAndServiceProvider_ServiceProviderId(
            Long wantedPostId,
            Long serviceProviderId
    );
}