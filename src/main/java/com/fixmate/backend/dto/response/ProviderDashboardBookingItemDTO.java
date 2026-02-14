package com.fixmate.backend.dto.response;

import com.fixmate.backend.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class ProviderDashboardBookingItemDTO {
    private Long bookingId;
    private BookingStatus status;
    private Instant scheduledAt;

    private String customerName;
    private String serviceTitle;

    private String city;
    private String address;

    private BigDecimal amount;
    private String paymentStatus;
}