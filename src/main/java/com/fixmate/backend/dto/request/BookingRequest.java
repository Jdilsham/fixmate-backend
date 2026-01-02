package com.fixmate.backend.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class BookingRequest {

     @NotNull
    private Long serviceId;

    @NotNull
    private Long providerId;

    @NotNull
    @Future(message = "Booking time must be in the future")
    private Instant scheduledAt;

    @NotNull
    private String address;

    @NotNull
    private String city;


}
