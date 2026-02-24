package com.racha.api.usecase.transaction;

import com.racha.api.domain.entity.Expense;
import com.racha.api.domain.entity.ExpenseSplit;
import com.racha.api.domain.entity.Transaction;
import com.racha.api.domain.enumeration.TransactionType;
import com.racha.api.domain.repository.ExpenseRepository;
import com.racha.api.domain.repository.ExpenseSplitRepository;
import com.racha.api.domain.repository.GroupMemberRepository;
import com.racha.api.domain.repository.TransactionRepository;
import com.racha.api.dto.transaction.TransactionDetailResponse;
import com.racha.api.expection.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetTransactionDetailUseCase {

    private final TransactionRepository transactionRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional(readOnly = true)
    public TransactionDetailResponse execute(UUID transactionId, UUID userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BusinessException("Transação não encontrada", HttpStatus.NOT_FOUND));

        if (!groupMemberRepository.existsByGroupIdAndUserId(transaction.getGroup().getId(), userId)) {
            throw new BusinessException("Usuário não tem permissão para visualizar esta transação", HttpStatus.FORBIDDEN);
        }

        TransactionDetailResponse response = TransactionDetailResponse.builder()
                .id(transaction.getId())
                .groupId(transaction.getGroup().getId())
                .groupName(transaction.getGroup().getName())
                .userId(transaction.getUser().getId())
                .userName(transaction.getUser().getName())
                .type(transaction.getType())
                .category(transaction.getCategory())
                .name(transaction.getName())
                .amount(transaction.getAmount())
                .createdAt(transaction.getCreatedAt())
                .expenseId(transaction.getExpenseId())
                .build();

        if (transaction.getExpenseId() != null) {
            Expense expense = expenseRepository.findById(transaction.getExpenseId())
                    .orElse(null);

            if (expense != null) {
                response.setExpenseTitle(expense.getTitle());
                response.setExpenseInvoice(expense.getInvoice());

                if (transaction.getType() == TransactionType.EXPENSE) {
                    List<ExpenseSplit> splits = expenseSplitRepository.findByExpenseId(expense.getId());
                    List<TransactionDetailResponse.ExpenseSplitDetail> splitDetails = splits.stream()
                            .map(split -> TransactionDetailResponse.ExpenseSplitDetail.builder()
                                    .userId(split.getUser().getId())
                                    .userName(split.getUser().getName())
                                    .amount(split.getAmount())
                                    .paid(split.getPaid())
                                    .paidAt(split.getPaidAt())
                                    .evidence(split.getEvidence())
                                    .build())
                            .collect(Collectors.toList());
                    response.setSplits(splitDetails);
                }
            }
        } else {
            response.setSplits(Collections.emptyList());
        }

        return response;
    }
}
