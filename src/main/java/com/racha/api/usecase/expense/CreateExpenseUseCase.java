package com.racha.api.usecase.expense;

import com.racha.api.domain.entity.Expense;
import com.racha.api.domain.entity.ExpenseSplit;
import com.racha.api.domain.entity.Group;
import com.racha.api.domain.entity.GroupMember;
import com.racha.api.domain.entity.User;
import com.racha.api.domain.repository.ExpenseRepository;
import com.racha.api.domain.repository.ExpenseSplitRepository;
import com.racha.api.domain.repository.GroupMemberRepository;
import com.racha.api.domain.repository.GroupRepository;
import com.racha.api.domain.repository.UserRepository;
import com.racha.api.dto.expense.CreateExpenseRequest;
import com.racha.api.dto.expense.ExpenseResponse;
import com.racha.api.expection.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreateExpenseUseCase {

    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public ExpenseResponse execute(UUID groupId, CreateExpenseRequest request, UUID userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException("Grupo não encontrado", HttpStatus.NOT_FOUND));

        if (group.getDeletedAt() != null) {
            throw new BusinessException("Grupo não encontrado", HttpStatus.NOT_FOUND);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND));

        groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new BusinessException("Usuário não é membro deste grupo", HttpStatus.FORBIDDEN));

        Set<UUID> userIdsToDivide = new HashSet<>();

        if (request.getDivideTo() == null || request.getDivideTo().isEmpty()) {
            List<GroupMember> allMembers = groupMemberRepository.findByGroupId(groupId);
            userIdsToDivide.addAll(allMembers.stream()
                    .map(member -> member.getUser().getId())
                    .collect(Collectors.toSet()));
        } else {
            userIdsToDivide.add(userId);
            for (UUID targetUserId : request.getDivideTo()) {
                if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, targetUserId)) {
                    throw new BusinessException("Usuário com ID " + targetUserId + " não pertence a este grupo", HttpStatus.BAD_REQUEST);
                }
                userIdsToDivide.add(targetUserId);
            }
        }

        Expense expense = Expense.builder()
                .group(group)
                .createdBy(user)
                .title(request.getTitle())
                .amount(request.getAmount())
                .build();

        expense = expenseRepository.save(expense);

        BigDecimal amountPerPerson = request.getAmount()
                .divide(BigDecimal.valueOf(userIdsToDivide.size()), 2, RoundingMode.HALF_UP);

        for (UUID targetUserId : userIdsToDivide) {
            User targetUser = userRepository.findById(targetUserId)
                    .orElseThrow(() -> new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND));

            boolean paid = targetUserId.equals(userId);

            ExpenseSplit expenseSplit = ExpenseSplit.builder()
                    .expense(expense)
                    .user(targetUser)
                    .amount(amountPerPerson)
                    .paid(paid)
                    .build();

            expenseSplitRepository.save(expenseSplit);
        }

        return ExpenseResponse.builder()
                .id(expense.getId())
                .groupId(expense.getGroup().getId())
                .createdById(expense.getCreatedBy().getId())
                .title(expense.getTitle())
                .amount(expense.getAmount())
                .createdAt(expense.getCreatedAt())
                .build();
    }
}

