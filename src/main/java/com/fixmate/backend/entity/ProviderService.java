package com.fixmate.backend.entity;

import com.fixmate.backend.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "provider_service",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"service_provider_id", "service_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProviderService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // PROVIDER
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_provider_id", nullable = false)
    private ServiceProvider serviceProvider;

    // SERVICE (dropdown selection)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Services service;

    // PROVIDER-SPECIFIC DETAILS
    @Column(columnDefinition = "TEXT")
    private String description;

    // PRICING
    @Column(name = "fixed_price", precision = 10, scale = 2)
    private BigDecimal fixedPrice;

    @Column(name = "hourly_rate", precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    // QUALIFICATION PROOF (PDF path)
    @Column(name = "qualification_doc")
    private String qualificationDoc;

    // SERVICE VERIFICATION
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    // STATUS
    @Column(name = "is_active")
    private Boolean isActive = true;
}
