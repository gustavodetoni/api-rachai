package com.racha.api.usecase.transaction;

import com.racha.api.domain.entity.Group;
import com.racha.api.domain.entity.Transaction;
import com.racha.api.domain.entity.User;
import com.racha.api.domain.enumeration.CategoryExpense;
import com.racha.api.domain.enumeration.TransactionType;
import com.racha.api.domain.repository.GroupRepository;
import com.racha.api.domain.repository.TransactionRepository;
import com.racha.api.domain.repository.UserRepository;
import com.racha.api.expection.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateTransactionUseCase {

    private final TransactionRepository transactionRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Transactional
    public Transaction execute(
            UUID groupId,
            UUID userId,
            TransactionType type,
            String category,
            String name,
            BigDecimal amount
    ) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException("Group not found with ID: " + groupId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found with ID: " + userId));

        Transaction transaction = Transaction.builder()
                .group(group)
                .user(user)
                .type(type)
                .category(category)
                .name(name)
                .amount(amount)
                .build();

        return transactionRepository.save(transaction);
    }
}

