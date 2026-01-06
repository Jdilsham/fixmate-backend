package com.fixmate.backend.service;

import com.fixmate.backend.dto.response.ServiceProviderCardDTO;

import java.util.List;

public interface ClientViewService {
    List<ServiceProviderCardDTO> getAllVerifiedAndAvailableProviders();
}
