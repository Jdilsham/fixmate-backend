package com.fixmate.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitiateEmailChangeRequest {

    @NotBlank
    private String currentPassword;

    @Email
    @NotBlank
    private String newEmail;
}
