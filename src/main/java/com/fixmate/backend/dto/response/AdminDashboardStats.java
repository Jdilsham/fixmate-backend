package com.fixmate.backend.dto.response;

public record AdminDashboardStats(
        long totalUsers,
        long totalProviders,
        long pendingApprovals,
        long totalBookings,
        java.math.BigDecimal totalEarnings
) {
}
