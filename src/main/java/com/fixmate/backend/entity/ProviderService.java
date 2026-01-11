package com.fixmate.backend.entity;

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

    // PROVIDER-SPECIFIC FIELDS
    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "estimated_time_minutes", nullable = false)
    private Integer estimatedTimeMinutes;

    @Column(name = "is_active")
    private Boolean isActive = true;
}

