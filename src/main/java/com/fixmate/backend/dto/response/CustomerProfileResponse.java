package com.fixmate.backend.dto.response;

import com.fixmate.backend.entity.Address;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
public class CustomerProfileResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}
