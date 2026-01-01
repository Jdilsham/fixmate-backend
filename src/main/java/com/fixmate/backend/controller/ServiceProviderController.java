package com.fixmate.backend.controller;

import com.fixmate.backend.dto.request.ProviderOnboardingRequest;
import com.fixmate.backend.dto.response.ProviderDashboardResponse;
import com.fixmate.backend.dto.response.ProviderProfileResponse;
import com.fixmate.backend.service.ServiceProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/provider")
@RequiredArgsConstructor
public class ServiceProviderController {

    private final ServiceProviderService service;

    @PostMapping("/onboard")
    public ResponseEntity<Void> onboard(Authentication auth,
                                        @Valid @RequestBody ProviderOnboardingRequest req) {
        service.onboardProvider(auth.getName(), req);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/availability")
    public ResponseEntity<Void> updateAvailability(Authentication auth,
                                                   @RequestParam boolean status) {
        service.updateAvailability(auth.getName(), status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<ProviderProfileResponse> myProfile(Authentication auth) {
        return ResponseEntity.ok(service.getMyProfile(auth.getName()));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ProviderDashboardResponse> dashboard(Authentication auth) {
        return ResponseEntity.ok(service.getMyDashboardStats(auth.getName()));
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<ProviderProfileResponse>> providersByService(
            @PathVariable Long serviceId) {
        return ResponseEntity.ok(service.getProvidersByService(serviceId));
    }
}
