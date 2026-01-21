package com.fixmate.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ReviewResponse {

    private Long ReviewId;
    private Integer rating;
    private String comment;
    private Instant createdAt;
    private Long userId;
}
