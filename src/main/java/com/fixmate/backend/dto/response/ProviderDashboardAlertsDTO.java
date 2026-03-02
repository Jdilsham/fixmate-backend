package com.fixmate.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProviderDashboardAlertsDTO {
    private long newRequests;
    private long paymentPending;
    private long todayJobs;

    private boolean availabilityOff;
    private boolean verificationPending;
}