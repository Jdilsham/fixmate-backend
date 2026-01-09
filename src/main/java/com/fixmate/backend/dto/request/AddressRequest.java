package com.fixmate.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AddressRequest {

    private String addressLine1;
    private String addressLine2;
    private String province;

    private String city;
    private BigDecimal latitude;
    private BigDecimal longitude;


}
