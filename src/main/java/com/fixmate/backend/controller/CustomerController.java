package com.fixmate.backend.controller;

import com.fixmate.backend.dto.request.BookingRequest;
import com.fixmate.backend.dto.request.CustomerUpdateReq;
import com.fixmate.backend.dto.response.BookingResponse;
import com.fixmate.backend.dto.response.CustomerProfileResponse;
import com.fixmate.backend.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

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

    @PostMapping("/bookings")
    public ResponseEntity<BookingResponse> createBooking(
            Authentication auth,
            @Valid @RequestBody BookingRequest req
    ){
        return ResponseEntity.ok(customerService.createBooking(auth.getName(), req));
    }

}
