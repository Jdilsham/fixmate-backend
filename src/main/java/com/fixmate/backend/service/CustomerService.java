package com.fixmate.backend.service;

import com.fixmate.backend.dto.request.BookingRequest;
import com.fixmate.backend.dto.request.ChangePasswordRequest;
import com.fixmate.backend.dto.request.CustomerUpdateReq;
import com.fixmate.backend.dto.request.ProviderSearchRequest;
import com.fixmate.backend.dto.response.BookingResponse;
import com.fixmate.backend.dto.response.CustomerProfileResponse;
import com.fixmate.backend.dto.response.ProviderSearchResponse;

import java.util.List;

public interface CustomerService {
    CustomerProfileResponse getProfile(String email);

    CustomerProfileResponse updateProfile(String email, CustomerUpdateReq req);

    BookingResponse createBooking(String email,BookingRequest dto);

    void changePassword(Long userId, ChangePasswordRequest request);


}
