package com.fixmate.backend.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRequest {

    @NotNull
    private Long customerId;


    @NotNull
    private Long serviceId;

    @NotNull
    private Long providerId;

    @NotNull
    private Long addressId;

    @Future
    @NotNull
    private LocalDateTime scheduledTime;

    @NotNull
    private String address;

    @NotNull
    private String city;


}
