package com.fixmate.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProviderServiceResponseDTO {

    private Long providerServiceId;
    private Long serviceId;
    private String title;

    private BigDecimal basePrice;
    private Integer estimatedTimeMinutes;
    private String description;
}

