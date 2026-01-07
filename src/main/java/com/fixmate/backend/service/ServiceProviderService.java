package com.fixmate.backend.service;

import com.fixmate.backend.dto.request.ProfileUpdateReq;
import com.fixmate.backend.dto.response.*;
import com.fixmate.backend.entity.ServiceProvider;

import java.util.List;

public interface ServiceProviderService {

    ProviderProfileDTO getProfile(Long userId);

    ProviderProfileDTO getProfileById(Long providerId, Long currentUserId);

    void updateDescription(Long providerId, Long userId, String description);

    void addServiceToProvider(Long serviceId, Long userId);

    void updateProfile(Long userId, ProfileUpdateReq req);

    void requestVerification(Long userId);

    List<ProviderBookingResponse> getBookings(Long userId);

    EarningSummaryDTO getEarnings(Long userId);

    ServiceProvider getVerifiedProviderByUserId(Long userId);

    boolean toggleAvailability(String email);
}
