package com.fixmate.backend.dto.response;

import com.fixmate.backend.enums.VerificationStatus;
import java.time.Instant;

public record AdminPendingProvider(
        Long providerId,
        String firstName,
        String lastName,
        String email,
        String phone,
        String skill,
        String experience,
        String licenseNumber,
        String profileImage,
        String workPdfUrl,
        VerificationStatus verificationStatus,
        Instant createdAt
) {}
