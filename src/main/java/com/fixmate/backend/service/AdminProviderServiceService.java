package com.fixmate.backend.service;

import com.fixmate.backend.dto.response.AdminPendingServiceResponse;
import com.fixmate.backend.enums.VerificationStatus;

import java.util.List;

public interface AdminProviderServiceService {

    void verifyProviderService(Long providerServiceId, VerificationStatus status);

    List<AdminPendingServiceResponse> getPendingProviderServices();

}
