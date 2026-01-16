package com.fixmate.backend.service;

import com.fixmate.backend.dto.request.AddressRequest;
import com.fixmate.backend.dto.request.ProfileUpdateReq;
import com.fixmate.backend.dto.response.*;
import com.fixmate.backend.entity.ServiceProvider;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ServiceProviderService {

    ProviderProfileDTO getProfile(Long userId);

    ProviderProfileDTO getProfileById(Long providerId, Long currentUserId);

    void updateDescription(Long providerId, Long userId, String description);

    void updateProfile(Long userId, ProfileUpdateReq req);

    void requestVerification(Long userId);

    List<ProviderBookingResponse> getBookings(Long userId);

    EarningSummaryDTO getEarnings(Long userId);

    ServiceProvider getVerifiedProviderByUserId(Long userId);

    boolean toggleAvailability(String email);

    AddressResponse addProviderAddress(Long userId, AddressRequest request);

    AddressResponse updateProviderAddress(Long userId, AddressRequest request);

    void updateProfilePicture(Long userId, MultipartFile profilePic);

}
