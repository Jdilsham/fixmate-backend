package com.fixmate.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicServiceCardResponse {

    private Long providerServiceId;
    private Long serviceId;

    private String serviceTitle;
    private String categoryName;
    private String providerName;

    private BigDecimal fixedPrice;
    private BigDecimal hourlyRate;

    private BigDecimal rating;
    private String location;
}
