package com.fixmate.backend.service;

import com.fixmate.backend.dto.request.AddressRequest;
import com.fixmate.backend.dto.response.AddressResponse;

public interface AddressService {
    AddressResponse addProfileAddress(Long userId, AddressRequest addressRequest);
}
