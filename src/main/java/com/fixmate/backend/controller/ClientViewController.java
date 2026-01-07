package com.fixmate.backend.controller;

import com.fixmate.backend.dto.response.ServiceProviderCardDTO;
import com.fixmate.backend.service.ClientViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/service-providers")
@RequiredArgsConstructor
public class ClientViewController {
    private final ClientViewService clientViewService;

    @GetMapping
    public List<ServiceProviderCardDTO> getAllVerifiedAndAvailableProviders() {
        return clientViewService.getAllVerifiedAndAvailableProviders();
    }
}
