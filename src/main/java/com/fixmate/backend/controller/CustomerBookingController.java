package com.fixmate.backend.controller;

import com.fixmate.backend.dto.request.BookingRequest;
import com.fixmate.backend.dto.response.CustomerBookingResponse;
import com.fixmate.backend.service.CustomerBookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/customer")
@RequiredArgsConstructor
public class CustomerBookingController {
    private final CustomerBookingService customerBookingService;

    @PostMapping("/bookings")
    public ResponseEntity<CustomerBookingResponse> createBooking(
            Authentication authentication,
            @Valid @RequestBody BookingRequest dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerBookingService.createBooking(authentication.getName(), dto));
    }

}
