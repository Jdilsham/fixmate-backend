package com.fixmate.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ServiceDetailResponse {
    private Long serviceId;
    private String title;
    private String categoryName;
}
