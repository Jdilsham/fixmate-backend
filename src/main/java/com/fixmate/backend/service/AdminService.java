package com.fixmate.backend.service;

import com.fixmate.backend.dto.response.AdminPendingProvider;
import com.fixmate.backend.entity.ServiceProvider;
import com.fixmate.backend.enums.VerificationStatus;
import com.fixmate.backend.repository.ServiceProviderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
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
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Provider not found"
                ));

        if (provider.getVerificationStatus() != VerificationStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provider is not pending verification"
            );
        }

        if (provider.getIdFrontUrl() == null ||
                provider.getIdBackUrl() == null ||
                provider.getWorkPdfUrl() == null ||
                provider.getSkill() == null || provider.getSkill().isBlank() ||
                provider.getExperience() == null || provider.getExperience().isBlank() ||
                provider.getDescription() == null || provider.getDescription().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provider profile is incomplete"
            );
        }

        provider.setVerificationStatus(VerificationStatus.APPROVED);
        provider.setIsVerified(true);
        provider.setIsAvailable(true);
    }


    public void rejectProvider(Long providerId, String reason) {

        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Provider not found"
                ));

        if (provider.getVerificationStatus() != VerificationStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provider is not pending verification"
            );
        }

        provider.setVerificationStatus(VerificationStatus.REJECTED);
        provider.setIsVerified(false);
        provider.setIsAvailable(false);

        // optional: log / store rejection reason later
    }

}
