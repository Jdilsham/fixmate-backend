package com.fixmate.backend.controller;

import com.fixmate.backend.dto.request.CustomerUpdateReq;
import com.fixmate.backend.dto.response.CustomerBookingResponse;
import com.fixmate.backend.dto.response.CustomerProfileResponse;
import com.fixmate.backend.entity.Booking;
import com.fixmate.backend.service.CustomerBookingService;
import com.fixmate.backend.service.CustomerService;
import com.fixmate.backend.service.ProviderBookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final ProviderBookingService providerBookingService;
    private final CustomerBookingService customerBookingService;


    @GetMapping("/me")
    public ResponseEntity<CustomerProfileResponse> getProfile(Authentication auth) {
        return ResponseEntity.ok(customerService.getProfile(auth.getName()));
    }

    @PutMapping("/me")
    public ResponseEntity<CustomerProfileResponse> updateProfile(
            Authentication auth,
            @Valid @RequestBody CustomerUpdateReq req
    ) {
        return ResponseEntity.ok(customerService.updateProfile(auth.getName(), req));

    }

    @GetMapping("/bookings")
    public ResponseEntity<List<CustomerBookingResponse>> getMyBookings(
            Authentication auth
    ) {
        String email = auth.getName();

        List<Booking> bookings =
                customerBookingService.getMyBookingsByEmail(email);

        List<CustomerBookingResponse> response =
                bookings.stream()
                        .map(CustomerBookingResponse::from)
                        .toList();

        return ResponseEntity.ok(response);
    }


    @PostMapping("/bookings/{bookingId}/mark-paid")
    public ResponseEntity<Void> markBookingPaid(
            @PathVariable Long bookingId,
            Authentication auth
    ) {
        providerBookingService.markAsPaid(
                bookingId,
                auth.getName()
        );
        return ResponseEntity.ok().build();
    }


}
