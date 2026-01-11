package com.fixmate.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddServiceRequestDTO {
    @NotNull
    private Long serviceId;  // dropdown selection

    @NotNull
    private BigDecimal basePrice;

    private String description;

    @NotNull
    private Integer estimatedTimeMinutes;
}
