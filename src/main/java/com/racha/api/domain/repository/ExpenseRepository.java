package com.racha.api.domain.repository;

import com.racha.api.domain.entity.Expense;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseRepository {
    Expense save(Expense expense);
    
    Optional<Expense> findById(UUID id);
    
    List<Expense> findByGroupId(UUID groupId);
}

