package com.fixmate.backend.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class FinalizeBookingRequest {

    // FIXED pricing
    private BigDecimal finalAmount;

    // HOURLY pricing
    private BigDecimal hourlyRate;
    private BigDecimal hoursWorked;
}
