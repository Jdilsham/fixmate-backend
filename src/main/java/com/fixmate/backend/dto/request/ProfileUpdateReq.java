package com.fixmate.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProfileUpdateReq {

    @NotBlank
    private String skill;

    @NotBlank
    private String experience;

    private String profileImageUrl;

    private String address;
    private String city;
}
