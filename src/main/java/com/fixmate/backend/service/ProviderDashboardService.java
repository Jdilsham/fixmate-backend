package com.fixmate.backend.service;

import com.fixmate.backend.dto.response.ProviderDashboardSummaryDTO;

public interface ProviderDashboardService {
    ProviderDashboardSummaryDTO getSummary(Long userId);
}