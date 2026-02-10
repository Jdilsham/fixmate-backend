package com.fixmate.backend.dto.response;

import com.fixmate.backend.enums.VerificationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class AdminProviderDetailResponse {
    private Long providerId;
    private String fullName;
    private String email;
    private String phone;
    private String skill;
    private String experience;
    private String licenceNumber;
    private String description;
    private String city;
    private String profileImage;
    private String workPdf;
    private String idFrontUrl;
    private String idBackUrl;
    private VerificationStatus verificationStatus;
    private boolean isProfileComplete;
    private Instant joinedAt;
}
