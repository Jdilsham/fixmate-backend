package com.fixmate.backend.dto.response;

public record AdminUserView(
        long id,
        String firstName,
        String lastName,
        String email,
        String role,
        boolean banned,
        java.time.Instant createdAt
) {
}
