package com.fixmate.backend.controller;

import com.fixmate.backend.entity.User;
import com.fixmate.backend.service.ServiceProviderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/provider/test")
public class ProviderTestController {

    private final ServiceProviderService serviceProviderService;

    public ProviderTestController(ServiceProviderService serviceProviderService) {
        this.serviceProviderService = serviceProviderService;
    }

    @GetMapping
    public String providerOnly(@AuthenticationPrincipal User user) {

        // 🔐 THIS LINE ENFORCES ADMIN APPROVAL
        serviceProviderService.getVerifiedProviderByUserId(user.getId());

        return "PROVIDER ACCESS OK";
    }
}
