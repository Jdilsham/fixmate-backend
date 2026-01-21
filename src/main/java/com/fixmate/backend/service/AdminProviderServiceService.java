package com.fixmate.backend.service;

import com.fixmate.backend.enums.VerificationStatus;

public interface AdminProviderServiceService {

    void verifyProviderService(Long providerServiceId, VerificationStatus status);

}
