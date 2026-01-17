package com.fixmate.backend.service;

import com.fixmate.backend.dto.request.CustomerUpdateReq;
import com.fixmate.backend.dto.response.CustomerProfileResponse;

public interface CustomerService {
    CustomerProfileResponse getProfile(String email);

    CustomerProfileResponse updateProfile(String email, CustomerUpdateReq req);

}
