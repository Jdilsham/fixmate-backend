package com.fixmate.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "service")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;

    private String title;
    private String description;

    @Column(name = "base_price")
    private BigDecimal basePrice;

    @Column(name = "duration_estimate")
    private String durationEstimate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false )
    private ServiceCategory category;

    @ManyToMany(mappedBy = "services", fetch = FetchType.LAZY)
    private Set<ServiceProvider> providers = new HashSet<>();

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = false)
    private Set<Booking> bookings = new HashSet<>();


}
