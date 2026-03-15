package com.fixmate.backend.dto.response;

import com.fixmate.backend.enums.VerificationStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AdminProviderServiceDetailResponse {

    private Long providerServiceId;

    private String providerName;
    private String providerEmail;

    private String serviceTitle;
    private String categoryName;

    private String description;
    private Boolean isFixedPrice;
    private BigDecimal hourlyRate;

    private String qualificationDocUrl;
    private VerificationStatus verificationStatus;
    private Boolean isActive;

    private String district;
}