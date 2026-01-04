package com.fixmate.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class EarningSummaryDTO {
    private BigDecimal totalEarnings;
}
