package com.fixmate.backend.service.impl;

import com.fixmate.backend.dto.request.AddServiceRequestDTO;
import com.fixmate.backend.entity.ProviderService;
import com.fixmate.backend.entity.ServiceProvider;
import com.fixmate.backend.entity.Services;
import com.fixmate.backend.exception.ResourceNotFoundException;
import com.fixmate.backend.repository.ProviderServiceRepository;
import com.fixmate.backend.repository.ServiceProviderRepository;
import com.fixmate.backend.repository.ServiceRepository;
import com.fixmate.backend.service.ProviderServiceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ProviderServiceServiceImpl implements ProviderServiceService {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceRepository serviceRepository;
    private final ProviderServiceRepository providerServiceRepository;

    @Override
    public void addServiceToProvider(Long userId, AddServiceRequestDTO dto) {

        // 1️⃣ Get provider by logged-in user
        ServiceProvider provider = serviceProviderRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Service provider not found")
                );

        // 2️⃣ Get service (dropdown selection)
        Services service = serviceRepository
                .findById(dto.getServiceId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Service not found")
                );

        // 3️⃣ Prevent duplicate service for same provider
        boolean alreadyExists =
                providerServiceRepository
                        .existsByServiceProvider_ServiceProviderIdAndService_ServiceId(
                                provider.getServiceProviderId(),
                                service.getServiceId()
                        );

        if (alreadyExists) {
            throw new IllegalStateException(
                    "This service is already added by the provider"
            );
        }

        // 4️⃣ Create provider-specific service
        ProviderService providerService = new ProviderService();
        providerService.setServiceProvider(provider);
        providerService.setService(service);
        providerService.setBasePrice(dto.getBasePrice());
        providerService.setDescription(dto.getDescription());
        providerService.setEstimatedTimeMinutes(dto.getEstimatedTimeMinutes());
        providerService.setIsActive(true);

        // 5️⃣ Save
        providerServiceRepository.save(providerService);
    }
}

