package com.fixmate.backend.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PublicServiceCardResponse {

    private Long providerServiceId;

    private String serviceTitle;
    private String categoryName;

    private String providerName;

    private BigDecimal fixedPrice;
    private BigDecimal hourlyRate;
}
