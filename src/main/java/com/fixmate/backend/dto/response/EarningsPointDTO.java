package com.fixmate.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class EarningsPointDTO {
    private String month;
    private BigDecimal total;
}