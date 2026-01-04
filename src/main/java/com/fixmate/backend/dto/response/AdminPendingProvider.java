package com.fixmate.backend.dto.response;

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
        Boolean verified,
        Instant createdAt
) {}
