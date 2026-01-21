package com.fixmate.backend.controller;

import com.fixmate.backend.dto.request.ReviewCreateRequest;
import com.fixmate.backend.dto.response.ReviewResponse;
import com.fixmate.backend.entity.Review;
import com.fixmate.backend.entity.User;
import com.fixmate.backend.service.CustomUserDetailsService;
import com.fixmate.backend.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<String> createReview(
            @RequestBody @Valid ReviewCreateRequest request,
            Authentication auth
    ){
        Long userId = getUserId(auth);
        reviewService.createReview(userId, request);
        return ResponseEntity.ok("Review submitted successfully");
    }

    @GetMapping("/provider/{id}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByProvider(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(reviewService.getReviewsForProvider(id));
    }

    @GetMapping("/provider/{id}/rating")
    public ResponseEntity<BigDecimal> getRating(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(reviewService.getAvgRating(id));
    }

    private Long getUserId(Authentication auth){
        User user = (User) auth.getPrincipal();
        return user.getId();
    }
}
