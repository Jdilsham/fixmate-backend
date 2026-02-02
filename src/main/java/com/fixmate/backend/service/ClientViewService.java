package com.fixmate.backend.service;

import com.fixmate.backend.dto.response.PublicServiceCardResponse;
import com.fixmate.backend.dto.response.ServiceProviderCardDTO;

import java.util.List;

public interface ClientViewService {
    List<ServiceProviderCardDTO> getAllVerifiedAndAvailableProviders();

    PublicServiceCardResponse getPublicServiceById(Long providerServiceId);

    List<String> getAvailableSlots(Long providerServiceId, String date);

}
