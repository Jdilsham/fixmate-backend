package com.fixmate.backend.service;

import com.fixmate.backend.dto.request.AddProviderServiceRequest;
import com.fixmate.backend.dto.response.ProviderBookingResponse;
import com.fixmate.backend.dto.response.ProviderServiceCardResponse;
import com.fixmate.backend.dto.response.PublicServiceCardResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProviderServiceService {

    void addServiceToProvider(
            Long userId,
            AddProviderServiceRequest dto,
            MultipartFile qualificationPdf
    );

    List<PublicServiceCardResponse> getApprovedServices();

    List<ProviderServiceCardResponse> getProviderServices(Long userId);

    void toggleActive(Long providerServiceId, Long userId);

}
