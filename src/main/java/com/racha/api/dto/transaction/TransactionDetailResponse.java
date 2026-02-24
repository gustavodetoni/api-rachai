package com.racha.api.dto.transaction;

import com.racha.api.domain.enumeration.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDetailResponse {
    private UUID id;
    private UUID groupId;
    private String groupName;
    private UUID userId;
    private String userName;
    private TransactionType type;
    private String category;
    private String name;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    
    // Detalhes extras dependendo do tipo da transação
    private UUID expenseId;
    private String expenseTitle;
    private String expenseInvoice;
    private List<ExpenseSplitDetail> splits; // Para EXPENSE
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExpenseSplitDetail {
        private UUID userId;
        private String userName;
        private BigDecimal amount;
        private boolean paid;
        private LocalDateTime paidAt;
        private String evidence;
    }
}
