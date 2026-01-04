package com.fixmate.backend.repository;

import com.fixmate.backend.entity.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {

    // Used for provider authentication & verification
    Optional<ServiceProvider> findByUserId(Long userId);

    List<ServiceProvider> findByIsVerifiedFalse();
}
