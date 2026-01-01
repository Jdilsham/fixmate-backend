package com.fixmate.backend.service.impl;

import com.fixmate.backend.dto.request.ProviderOnboardingRequest;
import com.fixmate.backend.dto.response.ProviderDashboardResponse;
import com.fixmate.backend.dto.response.ProviderProfileResponse;
import com.fixmate.backend.entity.Services;
import com.fixmate.backend.entity.ServiceProvider;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.enums.Role;
import com.fixmate.backend.exception.ResourceNotFoundException;
import com.fixmate.backend.mapper.ProviderMapper;
import com.fixmate.backend.repository.ServiceProviderRepository;
import com.fixmate.backend.repository.ServiceRepository;
import com.fixmate.backend.repository.UserRepository;
import com.fixmate.backend.service.ServiceProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ServiceProviderServiceImpl implements ServiceProviderService {

    private final UserRepository userRepository;
    private final ServiceProviderRepository providerRepository;
    private final ServiceRepository serviceRepository;
    private final ProviderMapper providerMapper;
    private final ServiceProviderRepository serviceProviderRepository;

    @Override
    public void onboardProvider(String email, ProviderOnboardingRequest req) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (providerRepository.findByUserEmail(email).isPresent()) {
            throw new IllegalStateException("Already a provider");
        }

        Set<Services> services = serviceRepository.findAllById(req.getServiceIds())
                .stream().collect(Collectors.toSet());

        ServiceProvider provider = new ServiceProvider();
        provider.setSkill(req.getSkill());
        provider.setExperience(req.getExperience());
        provider.setLicenseNumber(req.getLicenseNumber());
        provider.setUser(user);
        provider.setServices(services);

        user.setRole(Role.SERVICE_PROVIDER);

        providerRepository.save(provider);
    }

    @Override
    public void updateAvailability(String email, boolean status) {
        ServiceProvider provider = providerRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        provider.setIsAvailable(status);
    }

    @Override
    public ProviderProfileResponse getMyProfile(String email) {
        return providerMapper.toProfile(
                providerRepository.findByUserEmail(email)
                        .orElseThrow(() -> new ResourceNotFoundException("Provider not found"))
        );
    }

    @Override
    public ProviderDashboardResponse getMyDashboardStats(String email) {
        ServiceProvider provider = providerRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        long totalBookings = provider.getBookings().size();
        BigDecimal rating = provider.getRating() != null ? provider.getRating() : BigDecimal.ZERO;

        return new ProviderDashboardResponse(totalBookings, rating);
    }

    @Override
    public List<ProviderProfileResponse> getProvidersByService(Long serviceId) {
        return providerRepository.findAvailableVerifiedByServiceId(serviceId)
                .stream()
                .map(providerMapper::toProfile)
                .toList();
    }

    @Override
    public ServiceProvider getVerifiedProviderByUserId(Long userId) {

        ServiceProvider provider = serviceProviderRepository
                .findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service provider profile not found"
                ));

        if (!provider.getIsVerified()) {
            throw new AccessDeniedException(
                    "Your service provider account is not approved yet"
            );
        }

        return provider;
    }

}

