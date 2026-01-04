package com.fixmate.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentRequest {
    private Long bookingId;
    private BigDecimal amount;
    private String workedTime;
}
