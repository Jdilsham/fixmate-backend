package com.fixmate.backend.dto.response;

import com.fixmate.backend.enums.VerificationStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AdminPendingServiceResponse {

    private Long providerServiceId;

    private String providerName;
    private String providerEmail;

    private String serviceTitle;
    private String categoryName;

    private String description;
    private BigDecimal hourlyRate;
    private Boolean isFixedPrice;

    private String qualificationDocUrl;
    private VerificationStatus verificationStatus;
    private String district;
}
