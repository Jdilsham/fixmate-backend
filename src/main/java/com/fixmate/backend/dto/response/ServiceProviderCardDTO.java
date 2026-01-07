package com.fixmate.backend.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceProviderCardDTO {

    private Long id;
    private String fullName;
    private String skill;
    private BigDecimal rating;
    private String description;
    private String location;
    private String imageUrl;
    private Boolean isVerified;
}
