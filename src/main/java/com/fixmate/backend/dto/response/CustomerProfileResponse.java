package com.fixmate.backend.dto.response;

import com.fixmate.backend.entity.Address;

import java.time.Instant;
import java.util.Set;

public class CustomerProfileResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Instant createsAt;
    private Set<AddressResponse> addresses;
}
