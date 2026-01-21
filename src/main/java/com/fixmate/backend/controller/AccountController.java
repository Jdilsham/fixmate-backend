package com.fixmate.backend.controller;

import com.fixmate.backend.dto.request.InitiateEmailChangeRequest;
import com.fixmate.backend.dto.request.VerifyEmailChangeRequest;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.service.impl.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account/email")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/initiate")
    public ResponseEntity<?> initiate(
            @AuthenticationPrincipal User user,
            @RequestBody InitiateEmailChangeRequest request){

        accountService.initiateEmailChange(user, request);
        return ResponseEntity.ok("OTP sent to new email");

    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(
            @AuthenticationPrincipal User user,
            @RequestBody VerifyEmailChangeRequest request) {

        accountService.verifyEmailChange(user, request);
        return ResponseEntity.ok("Email updated successfully");
    }



}

