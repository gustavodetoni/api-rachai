package com.racha.api.dto.transaction;

import com.racha.api.domain.enumeration.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TransactionResponse {
    private UUID id;
    private UUID groupId;
    private UUID userId;
    private TransactionType type;
    private String category;
    private String name;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}
