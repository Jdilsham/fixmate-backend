package com.fixmate.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProviderProfessionalInfoRequest {

    @NotBlank
    private String skill;

    @NotBlank
    private String experience;

    @NotBlank
    private String description;
}
