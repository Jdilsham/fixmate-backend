package com.fixmate.backend.service;

import com.fixmate.backend.dto.request.ProviderOnboardingRequest;
import com.fixmate.backend.dto.request.ProviderUpdateReq;
import com.fixmate.backend.dto.response.ProviderDashboardResponse;
import com.fixmate.backend.dto.response.ProviderProfileResponse;
import com.fixmate.backend.entity.ServiceProvider;

import java.util.List;

public interface ServiceProviderService {

    void onboardProvider(String email, ProviderOnboardingRequest request);

    void updateAvailability(String email, boolean status);

    ProviderProfileResponse getMyProfile(String email);

    ProviderDashboardResponse getMyDashboardStats(String email);

    List<ProviderProfileResponse> getProvidersByService(Long serviceId);

    ServiceProvider getVerifiedProviderByUserId(Long userId);
}

