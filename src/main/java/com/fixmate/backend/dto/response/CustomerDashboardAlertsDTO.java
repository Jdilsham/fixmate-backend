package com.fixmate.backend.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDashboardAlertsDTO {
    private long paymentPending;
    private long bookingsToday;
    private long upcomingBookings;
}