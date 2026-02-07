package com.fixmate.backend.service;

import com.fixmate.backend.dto.request.ReviewCreateRequest;
import com.fixmate.backend.dto.response.ReviewResponse;

import java.math.BigDecimal;
import java.util.List;

public interface ReviewService {
    void createReview(Long userId, ReviewCreateRequest request);

    //void updateProviderRating(Long providerId);

    List<ReviewResponse> getReviewsForProvider(Long providerId);

    BigDecimal getAvgRating(Long providerId);
}
