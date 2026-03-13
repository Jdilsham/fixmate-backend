package com.fixmate.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PublicServiceCardResponse {

    private Long providerId;
    private Long providerServiceId;
    private Long serviceId;

    private String serviceTitle;
    private String serviceDescription;
    private String categoryName;

    private String providerName;
    private String providerProfileImage;

    private Boolean fixedPriceAvailable;

    private BigDecimal hourlyRate;

    private Double rating;
    private String location;

    public PublicServiceCardResponse(
            Long providerServiceId,
            Long providerId,
            Long serviceId,
            String serviceTitle,
            String serviceDescription,
            String categoryName,
            String providerName,
            String providerProfilePic,
            Boolean fixedPrice,
            BigDecimal hourlyRate,
            Double rating,
            String location
    ) {
        this.providerServiceId = providerServiceId;
        this.providerId = providerId;
        this.serviceId = serviceId;
        this.serviceTitle = serviceTitle;
        this.serviceDescription = serviceDescription;
        this.categoryName = categoryName;
        this.providerName = providerName;
        this.providerProfileImage = providerProfilePic;
        this.fixedPriceAvailable = fixedPrice;
        this.hourlyRate = hourlyRate;
        this.rating = rating;
        this.location = location;
    }
}
