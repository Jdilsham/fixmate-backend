package com.fixmate.backend.dto.request;

import com.fixmate.backend.enums.PricingType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class SmartBookingRequest {

    @NotNull
    private Long serviceId;

    @NotNull
    @Future(message = "Booking time must be in the future")
    private Instant scheduledAt;

    private String description;

    @NotNull
    private PricingType pricingType;

    private String addressLine1;
    private String addressLine2;
    private String province;
    private String city;
    private String phone;

    @NotNull(message = "Latitude is required")
    private BigDecimal latitude;

    @NotNull(message = "Longitude is required")
    private BigDecimal longitude;
}