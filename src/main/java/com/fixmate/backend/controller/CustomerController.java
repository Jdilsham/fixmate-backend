package com.fixmate.backend.controller;

import com.fixmate.backend.dto.request.BookingRequest;
import com.fixmate.backend.dto.request.ChangePasswordRequest;
import com.fixmate.backend.dto.request.CustomerUpdateReq;
import com.fixmate.backend.dto.request.ProviderSearchRequest;
import com.fixmate.backend.dto.response.BookingResponse;
import com.fixmate.backend.dto.response.CustomerProfileResponse;
import com.fixmate.backend.dto.response.ProviderSearchResponse;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.service.CustomerService;
import com.fixmate.backend.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            Authentication authentication,
            @Valid @RequestBody BookingRequest dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.createBooking(authentication.getName(), dto));
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ){
        User user = (User) authentication.getPrincipal();
        customerService.changePassword(user.getId(), request);
        return ResponseEntity.ok("Password changed successfully");
    }
}
