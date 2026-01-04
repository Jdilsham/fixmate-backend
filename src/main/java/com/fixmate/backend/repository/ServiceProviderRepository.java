package com.fixmate.backend.repository;

import com.fixmate.backend.entity.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {
    
    List<ServiceProvider> findByIsVerifiedFalse();

    Optional<ServiceProvider> findByUserId(Long userId);
}
