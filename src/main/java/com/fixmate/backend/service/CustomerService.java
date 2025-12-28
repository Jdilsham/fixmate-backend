package com.fixmate.backend.service;

import com.fixmate.backend.dto.response.CustomerProfileResponse;

public interface CustomerService {
    CustomerProfileResponse getProfile(String email);
}
