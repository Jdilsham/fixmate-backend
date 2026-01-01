package com.fixmate.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProviderProfileResponse {

    private String fullName;
    private String email;
    private String phone;

    private String skill;
    private String experience;
    private BigDecimal rating;
    private Boolean isAvailable;
    private Boolean isVerified;

    private List<ServiceDetailResponse> services;
}
