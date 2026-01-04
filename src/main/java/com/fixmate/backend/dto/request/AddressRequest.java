package com.fixmate.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AddressRequest {
    private String address;
    private String city;
    private BigDecimal latitude;
    private BigDecimal longitude;


}
