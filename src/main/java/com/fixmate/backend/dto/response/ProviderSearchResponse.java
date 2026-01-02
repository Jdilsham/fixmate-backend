package com.fixmate.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProviderSearchResponse {
    private Long providerId;
    private String name;
    private String serviceType;
    private String city;
    private Double rating;
    private String skill;
    private Boolean isAvailable;
    private String profileImage;
}
