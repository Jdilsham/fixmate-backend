package com.fixmate.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SpendingPointDTO {
    private String month;      // e.g. "Sep"
    private BigDecimal total;  // total spent that month
}