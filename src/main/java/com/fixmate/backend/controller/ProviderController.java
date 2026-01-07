package com.fixmate.backend.controller;

import com.fixmate.backend.dto.request.ProfileUpdateReq;
import com.fixmate.backend.dto.response.*;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.service.CustomUserDetailsService;
import com.fixmate.backend.service.ProviderBookingService;
import com.fixmate.backend.service.ServiceProviderService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/provider")
@RequiredArgsConstructor
public class ProviderController {

    private final ServiceProviderService providerService;
    private final ProviderBookingService bookingService;

    @GetMapping("/profile")
    public ProviderProfileDTO profile(Authentication auth) {
        return providerService.getProfile(getUserId(auth));
    }

    @GetMapping("/{id}")
    public ProviderProfileDTO getProviderProfile(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long currentUserId = null;

        if (authentication != null &&
                authentication.getPrincipal() instanceof User user) {
            currentUserId = user.getId();
        }

        return providerService.getProfileById(id, currentUserId);
    }


    @PutMapping("/profile")
    public void updateProfile(
            Authentication auth,
            @RequestBody ProfileUpdateReq req
    ) {
        providerService.updateProfile(getUserId(auth), req);
    }

    @PatchMapping("/availability")
    public Map<String, Boolean> toggleAvailability(Authentication authentication) {

        String email = authentication.getName();
        boolean status = providerService.toggleAvailability(email);

        return Map.of("isAvailable", status);
    }


    @PostMapping("/verify")
    public void requestVerification(Authentication auth) {
        providerService.requestVerification(getUserId(auth));
    }

    @GetMapping("/bookings")
    public List<ProviderBookingResponse> bookings(Authentication auth) {
        return providerService.getBookings(getUserId(auth));
    }

    @PostMapping("/bookings/{id}/confirm")
    public void confirmBooking(@PathVariable("id") Long bookingId, Authentication auth) {
        bookingService.confirmBookings(getUserId(auth), bookingId);
    }

    @PostMapping("/bookings/{id}/cancel")
    public void cancelBooking(@PathVariable("id") Long bookingId, @RequestParam String reason, Authentication auth) {
        bookingService.cancelBookings(getUserId(auth), bookingId, reason);
    }

    @GetMapping("/earnings")
    public EarningSummaryDTO earnings(Authentication auth) {
        return providerService.getEarnings(getUserId(auth));
    }

    @PostMapping("/services/{serviceId}")
    public void addServiceToProfile(
            @PathVariable Long serviceId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();

        providerService.addServiceToProvider(
                user.getId(),
                serviceId
        );

    }

    private Long getUserId(Authentication auth) {
        return ((User) auth.getPrincipal()).getId();
    }
}
