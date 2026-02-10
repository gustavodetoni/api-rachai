package com.racha.api.domain.repository;

import com.racha.api.domain.entity.ExpenseSplit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseSplitRepository {
    ExpenseSplit save(ExpenseSplit expenseSplit);

    <S extends ExpenseSplit> List<S> saveAll(Iterable<S> entities);

    Optional<ExpenseSplit> findById(UUID id);
    
    List<ExpenseSplit> findByUserIdAndGroupId(UUID userId, UUID groupId);
    
    List<ExpenseSplit> findByUserIdAndGroupIdAndPaidFalse(UUID userId, UUID groupId);
    
    List<ExpenseSplit> findByExpenseId(UUID expenseId);
    
    List<ExpenseSplit> findByGroupIdAndExpenseCreatedByAndPaidFalse(UUID groupId, UUID createdByUserId);
    
    List<ExpenseSplit> findByUserIdAndGroupIdAndPaidFalseWithExpense(UUID userId, UUID groupId);
    
    List<ExpenseSplit> findByGroupIdAndPaidFalse(UUID groupId);

    List<ExpenseSplit> findUnpaidSplitsBetweenUsers(UUID groupId, UUID debtorId, UUID creditorId);

    Optional<ExpenseSplit> findByIdWithUser(UUID id);
}

