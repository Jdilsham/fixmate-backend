package com.fixmate.backend.repository;

import com.fixmate.backend.dto.response.PublicServiceCardResponse;
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
    SELECT new com.fixmate.backend.dto.response.PublicServiceCardResponse(
        ps.id,                     
        sp.serviceProviderId,         
        s.serviceId,                  
        s.title,                     
        ps.description,              
        c.name,                      
        CONCAT(u.firstName, ' ', u.lastName), 
        u.profilePic,                
        ps.fixedPrice,              
        ps.hourlyRate,               
        AVG(r.rating),              
        a.city
    )
    FROM ProviderService ps
    JOIN ps.service s
    JOIN s.category c
    JOIN ps.serviceProvider sp
    JOIN sp.user u
    LEFT JOIN Address a ON a.user.id = u.id
    LEFT JOIN Review r ON r.serviceProvider.id = sp.serviceProviderId
    WHERE ps.verificationStatus = :status
      AND ps.isActive = true
      AND sp.isAvailable = true
      AND sp.isVerified = true
    GROUP BY
        ps.id,
        sp.serviceProviderId,
        s.serviceId,
        s.title,
        ps.description,
        c.name,
        u.firstName,
        u.lastName,
        u.profilePic,
        ps.fixedPrice,
        ps.hourlyRate,
        a.city
""")
    List<PublicServiceCardResponse> findPublicApprovedServices(
            VerificationStatus status
    );

}
