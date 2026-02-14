package com.fixmate.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ProviderDashboardSummaryDTO {
    private long totalBookings;
    private long activeJobs;
    private long completedJobs;
    private BigDecimal monthIncome;
    private BigDecimal yearIncome;

    private BigDecimal lifetimeIncome;
    private List<EarningsPointDTO> earningsLast6Months;

    private ProviderDashboardAlertsDTO alerts;

    private List<ProviderDashboardBookingItemDTO> todayBookings;
    private List<ProviderDashboardBookingItemDTO> upcomingBookings;

    private ProviderDashboardProfileHealthDTO profileHealth;

    private boolean ratingsEnabled;
}