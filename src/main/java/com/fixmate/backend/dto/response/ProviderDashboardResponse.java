package com.fixmate.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ProviderDashboardResponse {
    private long totalBookings;
    private BigDecimal averageRating;
}
