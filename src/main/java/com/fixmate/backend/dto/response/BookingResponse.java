package com.fixmate.backend.dto.response;

import com.fixmate.backend.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

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