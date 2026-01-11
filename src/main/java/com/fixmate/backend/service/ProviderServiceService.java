package com.fixmate.backend.service;

import com.fixmate.backend.dto.request.AddServiceRequestDTO;

public interface ProviderServiceService {
    void addServiceToProvider(Long userId, AddServiceRequestDTO dto);
}
