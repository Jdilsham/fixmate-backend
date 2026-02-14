package com.fixmate.backend.dto.response;

import com.fixmate.backend.enums.BookingStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDashboardBookingItemDTO {
    private Long bookingId;
    private String serviceName;
    private String providerName;
    private BookingStatus status;
    private LocalDateTime scheduledAt;
    private BigDecimal amount;
    private String pricingType;
}