package com.fixmate.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddProviderServiceRequest {

    @NotNull
    private Long serviceId;

    private String description;

    // Pricing (both allowed)
    @NotNull
    private Boolean fixedPriceAvailable;

    private BigDecimal hourlyRate;
}
