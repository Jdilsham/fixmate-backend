package com.fixmate.backend.controller;

import com.fixmate.backend.dto.request.ServiceCategoryRequest;
import com.fixmate.backend.dto.response.AdminDashboardStats;
import com.fixmate.backend.dto.response.AdminPendingProvider;
import com.fixmate.backend.dto.response.AdminUserView;
import com.fixmate.backend.dto.response.ServiceCategoryResponse;
import com.fixmate.backend.entity.ServiceProvider;
import com.fixmate.backend.enums.VerificationStatus;
import com.fixmate.backend.service.AdminProviderServiceService;
import com.fixmate.backend.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AdminProviderServiceService adminProviderServiceService;



    @GetMapping("/stats")
    public ResponseEntity<AdminDashboardStats> getStats(){
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserView>> getUsers(){
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PatchMapping("/users/{id}/toggle-ban")
    public ResponseEntity<Void> toggleBan(@PathVariable Long id){
        adminService.toggleUserBan(id);
        return ResponseEntity.noContent().build();
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

    @GetMapping("/categories")
    public ResponseEntity<List<ServiceCategoryResponse>> getAllCategories(){
        return ResponseEntity.ok(adminService.gatAllCategories());
    }

    @PostMapping("/categories")
    public ResponseEntity<Void> createCategory(@Valid @RequestBody ServiceCategoryRequest req){
        adminService.createCategory(req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<Void> updateCategory(@PathVariable Long id , @Valid @RequestBody ServiceCategoryRequest req){
        adminService.updateCategory(id,req);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/category/{id}")
    public  ResponseEntity<Void> deleteCategory(@PathVariable Long id){
        adminService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

}
