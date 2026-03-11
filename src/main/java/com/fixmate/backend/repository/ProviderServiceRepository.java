package com.fixmate.backend.repository;

import com.fixmate.backend.dto.response.PublicServiceCardResponse;
import com.fixmate.backend.entity.ProviderService;
import com.fixmate.backend.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;

public interface ProviderServiceRepository extends JpaRepository<ProviderService, Long> {
    boolean existsByServiceProvider_ServiceProviderIdAndService_ServiceId(
            Long providerId,
            Long serviceId
    );

    List<ProviderService> findByVerificationStatus(VerificationStatus status);

    @Query("""
        SELECT ps FROM ProviderService ps
        JOIN FETCH ps.serviceProvider sp
        JOIN FETCH sp.user
        JOIN FETCH ps.service s
        JOIN FETCH s.category
        LEFT JOIN FETCH ps.district
        WHERE ps.verificationStatus = :status
    """)
    List<ProviderService> findByVerificationStatusWithDetails(VerificationStatus status);

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
        ps.isFixedPrice,
        ps.hourlyRate,
        AVG(r.rating),
        d.name
    )
    FROM ProviderService ps
    JOIN ps.service s
    JOIN s.category c
    JOIN ps.serviceProvider sp
    JOIN sp.user u
    LEFT JOIN ps.district d
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
        ps.isFixedPrice,
        ps.hourlyRate,
        d.name
""")
    List<PublicServiceCardResponse> findPublicApprovedServices(VerificationStatus status);

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
        ps.isFixedPrice,
        ps.hourlyRate,
        AVG(r.rating),
        d.name
    )
    FROM ProviderService ps
    JOIN ps.service s
    JOIN s.category c
    JOIN ps.serviceProvider sp
    JOIN sp.user u
    LEFT JOIN ps.district d
    LEFT JOIN Review r ON r.serviceProvider.id = sp.serviceProviderId
    WHERE ps.id = :providerServiceId
      AND ps.verificationStatus = :status
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
        ps.isFixedPrice,
        ps.hourlyRate,
        d.name
""")
    Optional<PublicServiceCardResponse> findPublicServiceById(
            Long providerServiceId,
            VerificationStatus status
    );

    @Query("""
    SELECT ps
    FROM ProviderService ps
    JOIN FETCH ps.serviceProvider sp
    JOIN FETCH sp.user u
    JOIN FETCH ps.service s
    LEFT JOIN FETCH ps.district d
    WHERE s.serviceId = :serviceId
      AND ps.verificationStatus = com.fixmate.backend.enums.VerificationStatus.APPROVED
      AND ps.isActive = true
      AND sp.isAvailable = true
      AND sp.isVerified = true
""")
    List<ProviderService> findSmartBookingCandidates(@Param("serviceId") Long serviceId);

    @Query("""
    SELECT ps.id
    FROM ProviderService ps
    WHERE ps.serviceProvider.serviceProviderId = :providerId
      AND EXISTS (
          SELECT 1
          FROM Booking b
          WHERE b.providerService.id = ps.id
      )
""")
    List<Long> findBookedProviderServiceIds(@Param("providerId") Long providerId);

    @Query("""
    SELECT ps FROM ProviderService ps
    JOIN FETCH ps.serviceProvider sp
    JOIN FETCH sp.user u
    JOIN FETCH ps.service s
    JOIN FETCH s.category c
    LEFT JOIN FETCH ps.district d
    WHERE ps.id = :id
""")
    Optional<ProviderService> findByIdWithAdminDetails(@Param("id") Long id);

}
