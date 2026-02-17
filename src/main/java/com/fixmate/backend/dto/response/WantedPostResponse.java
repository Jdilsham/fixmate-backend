package com.fixmate.backend.dto.response;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class WantedPostResponse {
    private Long id;
    private String profession;
    private String description;
    private Integer requiredCount;
    private String location;
    private Long currentJoined;
    private String status;
}