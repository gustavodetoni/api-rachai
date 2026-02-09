package com.racha.api.dto.expense;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupSummaryResponse {
    private BigDecimal totalSpent;
    private BigDecimal totalToReceive;
    private BigDecimal totalToPay;
}

