package com.fixmate.backend.controller;

import com.fixmate.backend.dto.request.ChangePasswordRequest;
import com.fixmate.backend.dto.response.PublicServiceCardResponse;
import com.fixmate.backend.dto.response.UserMeResponse;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fixmate.backend.service.ProviderServiceService;


import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ProviderServiceService providerServiceService;

    @GetMapping("/me")
    public UserMeResponse me(@AuthenticationPrincipal User user) {

        return new UserMeResponse(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name()
        );
    }

    @PostMapping("/profile/image")
    public ResponseEntity<String> uploadProfileImage(
            @RequestParam MultipartFile file
    ) {
        return ResponseEntity.ok(userService.uploadProfileImage(file));
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ){
        User user = (User) authentication.getPrincipal();
        userService.changePassword(user.getId(), request);
        return ResponseEntity.ok("Password changed successfully");
    }

    @GetMapping("/services")
    public ResponseEntity<List<PublicServiceCardResponse>> getServices() {
        return ResponseEntity.ok(
                providerServiceService.getApprovedServices()
        );
    }

}

