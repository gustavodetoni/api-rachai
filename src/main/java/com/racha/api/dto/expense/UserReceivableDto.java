package com.racha.api.dto.expense;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class UserReceivableDto {
    private UUID payerId;
    private String payerName;
    private String payerThumbnailUrl;
    private BigDecimal amount;
}
