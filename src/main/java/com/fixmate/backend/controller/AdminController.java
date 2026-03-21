package com.fixmate.backend.controller;

import com.fixmate.backend.dto.request.ServiceCategoryRequest;
import com.fixmate.backend.dto.response.*;
import com.fixmate.backend.enums.VerificationStatus;
import com.fixmate.backend.service.AdminProviderServiceService;
import com.fixmate.backend.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.fixmate.backend.service.AdminDashboardPdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final AdminProviderServiceService adminProviderServiceService;
    private final AdminDashboardPdfService adminDashboardPdfService;


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

    @GetMapping("/provider-services/pending")
    public ResponseEntity<List<AdminPendingServiceResponse>> getPendingProviderServices() {
        return ResponseEntity.ok(adminProviderServiceService.getPendingProviderServices());
    }

    @PutMapping("/provider-services/{id}/verify")
    public ResponseEntity<String> verifyProviderService(
            @PathVariable Long id,
            @RequestParam VerificationStatus status
    ) {
        adminProviderServiceService.verifyProviderService(id, status);
        return ResponseEntity.ok("Provider service verification updated");
    }

//   admin category endpoints
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

//    pending provider get req
    @GetMapping("/providers/{id}")
    public ResponseEntity<AdminProviderDetailResponse> getProviderDetail(@PathVariable Long id){
        return  ResponseEntity.ok(adminService.getProviderDetails(id));
    }

//    rejecting a provider
    @PutMapping("/providers/{id}/reject")
    public ResponseEntity<Void> rejectProvider(@PathVariable Long id , @RequestBody String reason){
        adminService.rejectProvider(id, reason);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/provider-services/{id}")
    public ResponseEntity<AdminProviderServiceDetailResponse> getProviderServiceDetails(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(adminProviderServiceService.getProviderServiceDetails(id));
    }

    @GetMapping("/dashboard/export")
    public ResponseEntity<byte[]> exportAdminDashboardPdf() {
        byte[] pdf = adminDashboardPdfService.generateAdminDashboardPdf();

        String filename = "admin-dashboard-report.pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }

}


