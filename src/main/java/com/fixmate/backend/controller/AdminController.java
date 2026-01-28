package com.fixmate.backend.controller;

import com.fixmate.backend.dto.response.AdminPendingProvider;
import com.fixmate.backend.entity.ServiceProvider;
import com.fixmate.backend.enums.VerificationStatus;
import com.fixmate.backend.service.AdminProviderServiceService;
import com.fixmate.backend.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final AdminProviderServiceService adminProviderServiceService;

    public AdminController(
            AdminService adminService,
            AdminProviderServiceService adminProviderServiceService
    ) {
        this.adminService = adminService;
        this.adminProviderServiceService = adminProviderServiceService;
    }

    // View pending providers
    @GetMapping("/providers/pending")
    public ResponseEntity<List<AdminPendingProvider>> getPendingProviders() {
        return ResponseEntity.ok(adminService.getPendingProviders());
    }

    // Approve provider
    @PutMapping("/providers/{providerId}/approve")
    public ResponseEntity<String> approveProvider(@PathVariable Long providerId) {
        adminService.approveProvider(providerId);
        return ResponseEntity.ok("Service provider approved");
    }

    @PutMapping("/provider-services/{id}/verify")
    public ResponseEntity<String> verifyProviderService(
            @PathVariable Long id,
            @RequestParam VerificationStatus status
    ) {
        adminProviderServiceService.verifyProviderService(id, status);
        return ResponseEntity.ok("Provider service verification updated");
    }

    @PutMapping("/providers/{providerId}/reject")
    public ResponseEntity<Void> rejectProvider(
            @PathVariable Long providerId,
            @RequestParam(required = false) String reason
    ) {
        adminService.rejectProvider(providerId, reason);
        return ResponseEntity.ok().build();
    }

}
