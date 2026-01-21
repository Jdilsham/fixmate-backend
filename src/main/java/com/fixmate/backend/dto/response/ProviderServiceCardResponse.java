package com.fixmate.backend.dto.response;

import com.fixmate.backend.enums.VerificationStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProviderServiceCardResponse {

    private Long providerServiceId;

    private Long serviceId;
    private String serviceTitle;
    private String categoryName;

    private String description;

    private Boolean fixedPriceAvailable;
    private BigDecimal hourlyRate;

    private VerificationStatus verificationStatus;

    private Boolean isActive;

    private String qualificationDoc;
}
