package com.fixmate.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProviderSearchRequest {
    private String skill;
    private String serviceCategory;
    private String city;
    private Long serviceId;
}
