package com.fixmate.backend.controller;

import com.fixmate.backend.dto.request.ProfileUpdateReq;
import com.fixmate.backend.dto.response.*;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.service.ServiceProviderService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/provider")
@RequiredArgsConstructor
public class ProviderController {

    private final ServiceProviderService providerService;

    @GetMapping("/profile")
    public ProviderProfileDTO profile(Authentication auth) {
        return providerService.getProfile(getUserId(auth));
    }

    @PutMapping("/profile")
    public void updateProfile(
            Authentication auth,
            @RequestBody ProfileUpdateReq req
    ) {
        providerService.updateProfile(getUserId(auth), req);
    }

    @PostMapping("/verify")
    public void requestVerification(Authentication auth) {
        providerService.requestVerification(getUserId(auth));
    }

    @GetMapping("/bookings")
    public List<BookingResponse> bookings(Authentication auth) {
        return providerService.getBookings(getUserId(auth));
    }

    @GetMapping("/earnings")
    public EarningSummaryDTO earnings(Authentication auth) {
        return providerService.getEarnings(getUserId(auth));
    }

    private Long getUserId(Authentication auth) {
        return ((User) auth.getPrincipal()).getId();
    }
}
