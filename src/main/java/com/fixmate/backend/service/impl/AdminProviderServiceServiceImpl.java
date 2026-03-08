package com.fixmate.backend.service.impl;

import com.fixmate.backend.dto.response.AdminPendingServiceResponse;
import com.fixmate.backend.entity.ProviderService;
import com.fixmate.backend.enums.VerificationStatus;
import com.fixmate.backend.exception.ResourceNotFoundException;
import com.fixmate.backend.repository.ProviderServiceRepository;
import com.fixmate.backend.service.AdminProviderServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    @Transactional(readOnly = true)
    public List<AdminPendingServiceResponse> getPendingProviderServices() {
        List<ProviderService> pendingServices =
                providerServiceRepository.findByVerificationStatusWithDetails(VerificationStatus.PENDING);

        return pendingServices.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AdminPendingServiceResponse mapToResponse(ProviderService ps) {
        return AdminPendingServiceResponse.builder()
                .providerServiceId(ps.getId())
                .providerName(
                        ps.getServiceProvider().getUser().getFirstName() + " " +
                        ps.getServiceProvider().getUser().getLastName()
                )
                .providerEmail(ps.getServiceProvider().getUser().getEmail())
                .serviceTitle(ps.getService().getTitle())
                .categoryName(ps.getService().getCategory().getName())
                .description(ps.getDescription())
                .hourlyRate(ps.getHourlyRate())
                .isFixedPrice(ps.getIsFixedPrice())
                .qualificationDocUrl(ps.getQualificationDoc())
                .verificationStatus(ps.getVerificationStatus())
                .district(ps.getDistrict() != null ? ps.getDistrict().getName() : null)
                .build();
    }
}

