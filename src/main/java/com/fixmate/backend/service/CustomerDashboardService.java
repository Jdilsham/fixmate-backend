package com.fixmate.backend.service;

import com.fixmate.backend.dto.response.CustomerDashboardSummaryDTO;

public interface CustomerDashboardService {
    CustomerDashboardSummaryDTO getSummary(String email);
}