package com.fixmate.backend.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingDetailResponse {

    private Long bookingId;

    private String customerName;
    private String customerPhone;

    private String serviceTitle;
    private String description;

    private LocalDateTime scheduledAt;

    private BigDecimal paymentAmount;
    private String paymentType;

    private String address;
}
