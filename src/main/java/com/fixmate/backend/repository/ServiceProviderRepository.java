package com.fixmate.backend.repository;

import com.fixmate.backend.dto.response.AdminPendingProvider;
import com.fixmate.backend.entity.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.List;


public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {
    List<ServiceProvider> findByIsVerifiedFalse();

    Optional<ServiceProvider> findByUserId(Long userId);

    Optional<ServiceProvider> findByUserEmail(String email);

    List<ServiceProvider> findByIsAvailableTrueAndIsVerifiedTrue();

    Optional<ServiceProvider> findByServiceProviderIdAndIsVerifiedTrue(Long id);

    @Query("""
        SELECT new com.fixmate.backend.dto.response.AdminPendingProvider(
            sp.serviceProviderId,
            u.firstName,
            u.lastName,
            u.email,
            u.phone,
            sp.skill,
            sp.experience,
            sp.licenseNumber,
            sp.profileImage,
            sp.isVerified,
            u.createdAt
        )
        FROM ServiceProvider sp
        JOIN sp.user u
        WHERE sp.isVerified = false
    """)
    List<AdminPendingProvider> findPendingProvidersForAdmin();


}
