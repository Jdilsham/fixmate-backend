package com.fixmate.backend.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class BookingRequest {
   /* @NotNull
    private Long serviceId;
*/
    @NotNull
    private Long providerId;

    @NotNull
    @Future(message = "Booking time must be in the future")
    private Instant scheduledAt;

    private String description;

    //optional snapshot fallback enabled
    private String address;
    private String city;
    private String phone;
    private BigDecimal latitude;
    private BigDecimal longitude;


}
