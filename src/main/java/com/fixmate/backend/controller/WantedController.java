package com.fixmate.backend.controller;

import com.fixmate.backend.dto.request.WantedPostRequest;
import com.fixmate.backend.dto.response.WantedPostResponse;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.service.WantedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wanted")
@RequiredArgsConstructor
public class WantedController {
    private final WantedService wantedService;

    // 1. Get all advertisements (any authenticated user)
    @GetMapping
    public ResponseEntity<List<WantedPostResponse>> getAllPosts() {
        return ResponseEntity.ok(wantedService.getAllOpenPosts());
    }

    // 2. Publish an advertisement (Only Customers/Users)
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<WantedPostResponse> createPost(
            @RequestBody WantedPostRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(wantedService.createPost(request, user));
    }

    // 3. Sign up for work (Only Service Providers)
    @PostMapping("/{id}/apply")
    @PreAuthorize("hasRole('SERVICE_PROVIDER')")
    public ResponseEntity<String> applyForWork(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        wantedService.applyToPost(id, user);
        return ResponseEntity.ok("Successfully signed up for work!");
    }
}