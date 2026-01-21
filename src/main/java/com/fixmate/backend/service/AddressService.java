package com.fixmate.backend.service;

import com.fixmate.backend.dto.request.AddressRequest;
import com.fixmate.backend.dto.response.AddressResponse;

public interface AddressService {
    AddressResponse getProfileAddress(Long userId);

    AddressResponse addProfileAddress(Long userId, AddressRequest addressRequest);

    AddressResponse updateProfileAddress(Long userId, AddressRequest addressRequest);
}
