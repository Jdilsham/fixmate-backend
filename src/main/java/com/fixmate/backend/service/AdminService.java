package com.fixmate.backend.service;

import com.fixmate.backend.dto.response.AdminPendingProvider;
import com.fixmate.backend.entity.ServiceProvider;
import com.fixmate.backend.repository.ServiceProviderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AdminService {

    private final ServiceProviderRepository serviceProviderRepository;

    public AdminService(ServiceProviderRepository serviceProviderRepository) {
        this.serviceProviderRepository = serviceProviderRepository;
    }

    // View pending providers
    public List<AdminPendingProvider> getPendingProviders() {
        return serviceProviderRepository.findPendingProvidersForAdmin();
    }

    // Approve provider
    public void approveProvider(Long providerId) {
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found")
                );

        provider.setIsVerified(true);
        provider.setIsAvailable(true);

        serviceProviderRepository.save(provider);
    }
}
