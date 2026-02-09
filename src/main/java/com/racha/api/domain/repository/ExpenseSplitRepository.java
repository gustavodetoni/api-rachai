package com.racha.api.domain.repository;

import com.racha.api.domain.entity.ExpenseSplit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseSplitRepository {
    ExpenseSplit save(ExpenseSplit expenseSplit);
    
    Optional<ExpenseSplit> findById(UUID id);
    
    List<ExpenseSplit> findByUserIdAndGroupId(UUID userId, UUID groupId);
    
    List<ExpenseSplit> findByUserIdAndGroupIdAndPaidFalse(UUID userId, UUID groupId);
    
    List<ExpenseSplit> findByExpenseId(UUID expenseId);
    
    List<ExpenseSplit> findByGroupIdAndExpenseCreatedByAndPaidFalse(UUID groupId, UUID createdByUserId);
    
    List<ExpenseSplit> findByUserIdAndGroupIdAndPaidFalseWithExpense(UUID userId, UUID groupId);
    
    Optional<ExpenseSplit> findByIdWithUser(UUID id);
}

