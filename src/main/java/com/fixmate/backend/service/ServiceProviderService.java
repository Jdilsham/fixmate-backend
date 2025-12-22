package com.fixmate.backend.service;

import com.fixmate.backend.entity.ServiceProvider;
import com.fixmate.backend.repository.ServiceProviderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ServiceProviderService {

    private final ServiceProviderRepository serviceProviderRepository;

    public ServiceProviderService(ServiceProviderRepository serviceProviderRepository) {
        this.serviceProviderRepository = serviceProviderRepository;
    }

    // 🔐 COMMON CHECK METHOD
    public ServiceProvider getVerifiedProviderByUserId(Long userId) {

        ServiceProvider provider = serviceProviderRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Service provider profile not found")
                );

        if (!provider.getIsVerified()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Your service provider account is not approved yet"
            );
        }

        return provider;
    }
}
