package com.fixmate.backend.service.impl;

import com.fixmate.backend.entity.ProviderService;
import com.fixmate.backend.enums.VerificationStatus;
import com.fixmate.backend.exception.ResourceNotFoundException;
import com.fixmate.backend.repository.ProviderServiceRepository;
import com.fixmate.backend.service.AdminProviderServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminProviderServiceServiceImpl
        implements AdminProviderServiceService {

    private final ProviderServiceRepository providerServiceRepository;

    @Override
    public void verifyProviderService(
            Long providerServiceId,
            VerificationStatus status
    ) {

        ProviderService providerService = providerServiceRepository
                .findById(providerServiceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Provider service not found")
                );

        if (status != VerificationStatus.APPROVED &&
                status != VerificationStatus.REJECTED) {
            throw new IllegalArgumentException("Invalid verification status");
        }

        providerService.setVerificationStatus(status);
    }
}
