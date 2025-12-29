package com.fixmate.backend.dto.response;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class AddressResponse {
    private Long id;

    private String address;
    private String city;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
