package com.fixmate.backend.service;

import com.fixmate.backend.dto.request.ChangePasswordRequest;
import com.fixmate.backend.dto.request.CustomerUpdateReq;
import com.fixmate.backend.dto.response.CustomerProfileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CustomerService {
    CustomerProfileResponse getProfile(String email);

    CustomerProfileResponse updateProfile(String email, CustomerUpdateReq req);

    void changePassword(Long userId, ChangePasswordRequest request);

}
