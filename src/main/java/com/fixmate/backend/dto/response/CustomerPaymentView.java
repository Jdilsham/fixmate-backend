package com.fixmate.backend.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class CustomerPaymentView {

    private Long paymentId;
    private Long bookingId;

    private String providerName;
    private String serviceName;

    private String workedTime;
    private BigDecimal amount;

    private String paymentStatus;
    private String bookingStatus;
}
