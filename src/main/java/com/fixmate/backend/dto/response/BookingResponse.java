package com.fixmate.backend.dto.response;

import com.fixmate.backend.enums.BookingStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingDetailResponse {

    private Long bookingId;
    private BookingStatus status;

    private String customerName;
    private String customerPhone;

    private String serviceTitle;
    private String description;

    private LocalDateTime scheduledAt;

    private BigDecimal paymentAmount;
    private String paymentType;

    private String address;
}
