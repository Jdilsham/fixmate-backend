package com.fixmate.backend.controller;

import com.fixmate.backend.dto.request.EmailVerifyRequest;
import com.fixmate.backend.dto.request.GoogleLoginRequest;
import com.fixmate.backend.dto.request.LoginRequest;
import com.fixmate.backend.dto.request.SignupRequest;
import com.fixmate.backend.dto.response.AuthResponse;
import com.fixmate.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        authService.signup(request);
        return ResponseEntity.ok(
                "Signup successful. Please verify your email."
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(
                authService.login(request)
        );
    }


    @PostMapping("/verify")
    public ResponseEntity<String> verify(
            @Valid @RequestBody EmailVerifyRequest request
    ) {
        authService.verifyUser(
                request.getEmail(),
                request.getCode()
        );
        return ResponseEntity.ok(
                "Email verified successfully"
        );
    }

    @PostMapping("/google")
    public ResponseEntity<Map<String, String>> googleLogin(
            @Valid @RequestBody GoogleLoginRequest request
    ) {
        String token = authService.googleLogin(
                request.getIdToken()
        );

        return ResponseEntity.ok(
                Map.of("token", token)
        );
    }
}
