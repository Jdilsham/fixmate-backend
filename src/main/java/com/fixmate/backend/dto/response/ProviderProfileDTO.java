package com.fixmate.backend.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;


@Data
@Builder
public class ProviderProfileDTO {
    private Long providerId;
    private String fullName;
    private String description;
    private String skill;
    private BigDecimal rating;
    private Boolean isVerified;
    private Boolean isAvailable;
    private String profileImage;
    private String city;
    private String phone;

    private List<ProviderServiceResponseDTO> services;
    private Boolean isOwner;
}
