package com.fixmate.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfileUpdateReq {

    @NotBlank
    private String skill;

    @NotBlank
    private String experience;

    private String profileImageUrl;

    private String addressLine1;
    private String city;
}
