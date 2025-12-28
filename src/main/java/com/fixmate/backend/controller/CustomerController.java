package com.fixmate.backend.controller;

import com.fixmate.backend.dto.response.CustomerProfileResponse;
import com.fixmate.backend.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping("/me")
    public ResponseEntity<CustomerProfileResponse> getProfile(Authentication auth) {
        return ResponseEntity.ok(customerService.getProfile(auth.getName()));
    }
}
