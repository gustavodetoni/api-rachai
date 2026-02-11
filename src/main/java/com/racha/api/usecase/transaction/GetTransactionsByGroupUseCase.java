package com.racha.api.usecase.transaction;

import com.racha.api.domain.entity.Transaction;
import com.racha.api.domain.enumeration.TransactionType;
import com.racha.api.domain.repository.TransactionRepository;
import com.racha.api.dto.transaction.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetTransactionsByGroupUseCase {

    private final TransactionRepository transactionRepository;

    public List<TransactionResponse> execute(UUID groupId, UUID authenticatedUserId) {
        List<Transaction> allTransactions = transactionRepository.findByGroupId(groupId);

        return allTransactions.stream()
                .filter(transaction ->
                        transaction.getType().equals(TransactionType.EXPENSE) ||
                        (!transaction.getType().equals(TransactionType.EXPENSE) &&
                                transaction.getUser().getId().equals(authenticatedUserId))
                )
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .groupId(transaction.getGroup().getId())
                .userId(transaction.getUser().getId())
                .type(transaction.getType())
                .category(transaction.getCategory() != null ? transaction.getCategory() : null)
                .name(transaction.getName())
                .amount(transaction.getAmount())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
