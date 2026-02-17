package com.fixmate.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WantedPostRequest {
    private String profession;
    private String description;
    private Integer requiredCount;
    private String location;
}