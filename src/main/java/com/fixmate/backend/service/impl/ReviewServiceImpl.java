package com.fixmate.backend.service.impl;

import com.fixmate.backend.dto.request.ReviewCreateRequest;
import com.fixmate.backend.dto.response.ReviewResponse;
import com.fixmate.backend.entity.Booking;
import com.fixmate.backend.entity.Review;
import com.fixmate.backend.entity.ServiceProvider;
import com.fixmate.backend.enums.BookingStatus;
import com.fixmate.backend.repository.BookingRepository;
import com.fixmate.backend.repository.ReviewRepository;
import com.fixmate.backend.repository.ServiceProviderRepository;
import com.fixmate.backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final ServiceProviderRepository serviceProviderRepository;

    @Override
    public void createReview(Long userId, ReviewCreateRequest request){

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Booking not found"
                ));

        if(!booking.getUser().getId().equals(userId)){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User has no authorization"
            );
        }

        if(booking.getStatus() != BookingStatus.COMPLETED){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only completed jobs can be reviewed"
            );
        }

        if(booking.getReview() != null){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Review already exists"
            );
        }

        Review review = new Review();

        review.setRating(BigDecimal.valueOf(request.getRating()));
        review.setComment(request.getComment());
        review.setUser(booking.getUser());
        review.setServiceProvider(booking.getServiceProvider());
        review.setBooking(booking);

        reviewRepository.save(review);

        updateProviderRating(booking.getServiceProvider().getServiceProviderId());
    }

    private void updateProviderRating(Long providerId){
        BigDecimal avgRating = reviewRepository.calculateAvgRating(providerId);

        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        provider.setRating(avgRating);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsForProvider(Long providerId){
        return reviewRepository.findByServiceProvider_ServiceProviderId(providerId)
                .stream()
                .map(r -> new ReviewResponse(
                        r.getReviewId(),
                        r.getRating().intValue(),
                        r.getComment(),
                        r.getCreatedAt(),
                        r.getUser().getId()
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAvgRating(Long providerId){
        return reviewRepository.calculateAvgRating(providerId);
    }
}
