package com.fixmate.backend.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProviderProfileDTO {
    private String fullName;
    private String skill;
    private BigDecimal rating;
    private Boolean isVerified;
}
