package com.fixmate.backend.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDashboardSummaryDTO {

    // KPI counts
    private long totalBookings;
    private long activeBookings;
    private long completedBookings;
    private long cancelledBookings;
    private long rejectedBookings;
    private long pendingBookings;
    private long paymentPendingBookings;

    // Spending
    private BigDecimal monthSpending;
    private BigDecimal yearSpending;
    private BigDecimal lifetimeSpending;

    // Chart
    private List<SpendingPointDTO> last6MonthsSpending;

    // Alerts
    private CustomerDashboardAlertsDTO alerts;

    // Lists
    private List<CustomerDashboardBookingItemDTO> todayBookings;
    private List<CustomerDashboardBookingItemDTO> upcomingBookings;

    // Profile health
    private CustomerDashboardProfileHealthDTO profileHealth;
}