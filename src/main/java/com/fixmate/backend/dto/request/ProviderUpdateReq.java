package com.fixmate.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data

public class ProviderUpdateReq {

    @NotBlank
    private String skill;

    @NotBlank
    private String experience;

    @NotNull
    private Boolean isAvailable;
}

