package com.fixmate.backend.controller;

import com.fixmate.backend.entity.ServiceProvider;
import com.fixmate.backend.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // 🔍 View pending providers
    @GetMapping("/providers/pending")
    public ResponseEntity<List<ServiceProvider>> getPendingProviders() {
        return ResponseEntity.ok(adminService.getPendingProviders());
    }

    // ✅ Approve provider
    @PutMapping("/providers/{providerId}/approve")
    public ResponseEntity<String> approveProvider(@PathVariable Long providerId) {
        adminService.approveProvider(providerId);
        return ResponseEntity.ok("Service provider approved");
    }
}
