package com.racha.api.usecase.expense;

import com.racha.api.domain.entity.Expense;
import com.racha.api.domain.entity.ExpenseSplit;
import com.racha.api.domain.entity.Group;
import com.racha.api.domain.repository.ExpenseRepository;
import com.racha.api.domain.repository.ExpenseSplitRepository;
import com.racha.api.domain.repository.GroupMemberRepository;
import com.racha.api.domain.repository.GroupRepository;
import com.racha.api.dto.expense.GroupSummaryResponse;
import com.racha.api.expection.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetGroupSummaryUseCase {

    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional(readOnly = true)
    public GroupSummaryResponse execute(UUID groupId, UUID userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException("Grupo não encontrado", HttpStatus.NOT_FOUND));

        if (group.getDeletedAt() != null) {
            throw new BusinessException("Grupo não encontrado", HttpStatus.NOT_FOUND);
        }

        groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new BusinessException("Usuário não é membro deste grupo", HttpStatus.FORBIDDEN));

        List<Expense> expenses = expenseRepository.findByGroupId(groupId);
        BigDecimal totalSpent = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<ExpenseSplit> splitsToReceive = expenseSplitRepository.findByGroupIdAndExpenseCreatedByAndPaidFalse(groupId, userId);
        BigDecimal totalToReceive = splitsToReceive.stream()
                .map(ExpenseSplit::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<ExpenseSplit> unpaidSplits = expenseSplitRepository.findByUserIdAndGroupIdAndPaidFalse(userId, groupId);
        BigDecimal totalToPay = unpaidSplits.stream()
                .map(ExpenseSplit::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return GroupSummaryResponse.builder()
                .totalSpent(totalSpent)
                .totalToReceive(totalToReceive)
                .totalToPay(totalToPay)
                .build();
    }
}

