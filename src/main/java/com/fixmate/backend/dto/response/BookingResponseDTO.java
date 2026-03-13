package com.fixmate.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class BookingResponseDTO {
    private Long bookingId;
    private BigDecimal totalPrice;
    private Instant createdAt;
    private String status;
    private String cancelReason;
    private String description;
    private UserSummaryDTO user;
}
