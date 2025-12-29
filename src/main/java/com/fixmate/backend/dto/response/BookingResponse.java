package com.fixmate.backend.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingResponse {
    private Long bookingId;
    private String status;
    private double price;
    private String providerName;
    private String serviceName;
    private LocalDateTime scheduledAt;


}
