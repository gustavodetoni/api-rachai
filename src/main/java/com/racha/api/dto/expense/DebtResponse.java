package com.racha.api.dto.expense;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class DebtResponse {
    private UUID userId;
    private String userName;
    private String userPix;
    private BigDecimal totalAmount;
}

