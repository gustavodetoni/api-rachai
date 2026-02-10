package com.racha.api.dto.expense;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class DebtResponse {
    private List<UUID> expenseSplitIds;
    private UUID userId;
    private String userName;
    private String userPix;
    private BigDecimal totalAmount;
}

