package com.fixmate.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyEmailChangeRequest {
    @NotBlank
    private String otp;
}
