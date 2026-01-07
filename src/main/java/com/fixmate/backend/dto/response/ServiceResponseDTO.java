package com.fixmate.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ServiceResponseDTO {
    private Long serviceId;
    private String title;
    private String description;
    private BigDecimal basePrice;
    private String durationEstimate;
}
