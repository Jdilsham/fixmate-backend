package com.fixmate.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class ProviderOnboardingRequest {

    @NotBlank
    private String skill;

    @NotBlank
    private String experience;

    private String licenseNumber;

    @NotEmpty
    private List<Long> serviceIds;
}

