package com.fixmate.backend.controller;

import com.fixmate.backend.dto.request.ChangePasswordRequest;
import com.fixmate.backend.dto.request.CustomerUpdateReq;
import com.fixmate.backend.dto.response.CustomerProfileResponse;
import com.fixmate.backend.dto.response.ProfileImageUploadRes;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


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

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ){
        User user = (User) authentication.getPrincipal();
        customerService.changePassword(user.getId(), request);
        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("profile-image")
    public ResponseEntity<ProfileImageUploadRes> uploadProfileImage(@RequestParam("file") MultipartFile file){
        String imageUrl = customerService.uploadProfileImage(file);
        return ResponseEntity.ok(new ProfileImageUploadRes(imageUrl));
    }
}
